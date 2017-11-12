package com.fri.rso.fririders.users.service;

import com.fri.rso.fririders.users.entity.User;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.List;

@RequestScoped
public class UserService {

    @PersistenceContext
    private EntityManager entityManager;

    private Client httpClient;

    private String accommodationsUrl = "http://localhost:8085/accommodations/all";

    public List<User> getUsers() {
        List<User> users = entityManager.createNamedQuery("User.findAll", User.class).getResultList();

        return users;
    }

    public User findById(String id) {
        try {
            return entityManager.createNamedQuery("User.findById", User.class).setParameter("id", id).getSingleResult();
        } catch (NoResultException e) {
            return  null;
        }
    }

    public User findByEmail(String email) {
        try {
            return entityManager.createNamedQuery("User.findByEmail", User.class).setParameter("email", email).getSingleResult();
        } catch (NoResultException e) {
            return  null;
        }
    }

    @Transactional
    public boolean createUser(User user) {
        try {
            beginTransaction();
            entityManager.persist(user);
            commitTransaction();

            return  true;
        } catch (Exception e) {
            rollbackTransaction();
            System.out.println(e.getMessage());

            return false;
        }
    }

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
            return httpClient
                    .target("http://localhost:8085/accommodations/all")
//                    .target(accommodationsUrl)
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<List<Object>>() {});
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
