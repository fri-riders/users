package com.fri.rso.fririders.users.service;

import com.fri.rso.fririders.users.entity.User;
import com.fri.rso.fririders.users.util.PasswordAuthentication;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.metrics.annotation.Counted;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@RequestScoped
@Log
public class UsersService {

    private static final Logger log = LogManager.getLogger(UsersService.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    public List<User> getUsers() {
        return entityManager.createNamedQuery("User.findAll", User.class).getResultList();
    }

    public User findById(String id) {
        try {
            return entityManager.createNamedQuery("User.findById", User.class).setParameter("id", id).getSingleResult();
        } catch (NoResultException e) {
            log.error(e.getMessage());

            return null;
        }
    }

    public User findByEmail(String email) {
        try {
            return entityManager.createNamedQuery("User.findByEmail", User.class).setParameter("email", email).getSingleResult();
        } catch (NoResultException e) {
            log.error(e.getMessage());

            return null;
        }
    }

    @Transactional
    @Counted(name = "insert_user_counter")
    public User createUser(User user) {
        try {
            beginTransaction();
            user.setPassword(new PasswordAuthentication().hash(user.getPassword().toCharArray()));
            entityManager.persist(user);
            commitTransaction();

            return user;
        } catch (Exception e) {
            rollbackTransaction();

            log.error(e.getMessage());

            return null;
        }
    }

    @Transactional
    @Counted(name = "update_user_counter")
    public User updateUser(User existing, User updated) {
        try {
            beginTransaction();

            if (updated.getFirstName() != null && !updated.getFirstName().equals(""))
                existing.setFirstName(updated.getFirstName());
            if (updated.getLastName() != null && !updated.getLastName().equals(""))
                existing.setLastName(updated.getLastName());

            existing.setUpdatedAt(new Date());

            commitTransaction();

            return existing;
        } catch (Exception e) {
            rollbackTransaction();

            log.error(e.getMessage());

            return null;
        }
    }

    @Transactional
    @Counted(name = "delete_user_counter")
    public boolean deleteUser(String id) {
        User user = entityManager.find(User.class, id);

        if (user == null){
            return false;
        }

        try {
            beginTransaction();
            entityManager.remove(user);
            commitTransaction();

            return true;
        } catch (Exception e) {
            rollbackTransaction();

            log.error(e.getMessage());

            return false;
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
