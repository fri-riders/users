package com.fri.rso.fririders.users.service;

import com.fri.rso.fririders.users.entity.User;
import com.fri.rso.fririders.users.resource.Helpers;
import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.metrics.annotation.Counted;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequestScoped
@Log
public class UserService {

    @PersistenceContext
    private EntityManager entityManager;

    private Client http = ClientBuilder.newClient();

    @Inject
    @DiscoverService(value = "accommodations", version = "*", environment = "")
    private Optional<String> accommodationsUrl;

    public List<User> getUsers() {
        return entityManager.createNamedQuery("User.findAll", User.class).getResultList();
    }

    public User findById(String id) {
        try {
            return entityManager.createNamedQuery("User.findById", User.class).setParameter("id", id).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public User findByEmail(String email) {
        try {
            return entityManager.createNamedQuery("User.findByEmail", User.class).setParameter("email", email).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Transactional
    @Counted(name = "insert_user_counter")
    public boolean createUser(User user) {
        try {
            beginTransaction();
            entityManager.persist(user);
            commitTransaction();

            return true;
        } catch (Exception e) {
            rollbackTransaction();
            System.out.println(e.getMessage());

            return false;
        }
    }

    @Transactional
    public boolean deleteUser(String id) {
        User user = entityManager.find(User.class, id);

        if (user == null){
            return false;
        }

        try {
            beginTransaction();
            entityManager.remove(user);
            commitTransaction();

            return  true;
        } catch (Exception e) {
            rollbackTransaction();
            System.out.println(e.getMessage());

            return false;
        }
    }

    public List<Object> findAccommodations(String userId) {
        try {
            System.out.println("accommodationsUrl = " + accommodationsUrl);
            if (accommodationsUrl.isPresent()) {
                return http.target(this.accommodationsUrl.get() + "/accommodations/all")
                        .request(MediaType.APPLICATION_JSON)
                        .get(new GenericType<List<Object>>() {});
            } else {
                ArrayList<Object> error = new ArrayList<>();
                error.add(Helpers.jsonToMap(Helpers.buildErrorJson("Accommodations service is unreachable.")));

                return error;
            }
        } catch (WebApplicationException | ProcessingException e) {
            System.out.println(e.getMessage());

            return null;
        }
    }

    private void beginTransaction() {
        if (!entityManager.getTransaction().isActive())
            entityManager.getTransaction().begin();
    }

    private void commitTransaction() {
        if (entityManager.getTransaction().isActive())
            entityManager.getTransaction().commit();
    }

    private void rollbackTransaction() {
        if (entityManager.getTransaction().isActive())
            entityManager.getTransaction().rollback();
    }
}
