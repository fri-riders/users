package com.fri.rso.fririders.users.service;

import com.fri.rso.fririders.users.entity.User;
import com.fri.rso.fririders.users.resource.Helpers;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.discovery.enums.AccessType;
import com.kumuluz.ee.discovery.utils.DiscoveryUtil;
import com.kumuluz.ee.fault.tolerance.annotations.CommandKey;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.metrics.annotation.Counted;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequestScoped
@Log
public class UserService {

    private static final Logger log = LogManager.getLogger(UserService.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    private Client http = ClientBuilder.newClient();

    @Inject
    private DiscoveryUtil discoveryUtil;

    @Inject
    @DiscoverService(value = "accommodations", version = "*", environment = "dev", accessType = AccessType.DIRECT)
    private Optional<String> accommodationsUrl;

    @Inject
    @DiscoverService(value = "display-bookings", version = "*", environment = "dev", accessType = AccessType.DIRECT)
    private Optional<String> bookingsUrl;

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

    @CircuitBreaker
    @Fallback(fallbackMethod = "findAccommodationsFallback")
    @CommandKey("http-find-accommodations")
    @Timeout(value = 1, unit = ChronoUnit.SECONDS)
    @Asynchronous
    public List<Object> findAccommodations(String userId) {
        try {
            log.info("accommodationsUrl = " + accommodationsUrl);
            log.info("accommodationsUrl.isPresent() = " + accommodationsUrl.isPresent());

            if (accommodationsUrl.isPresent()) {
                return http.target(this.accommodationsUrl.get() + "/accommodations/all")
                        .request(MediaType.APPLICATION_JSON)
                        .get(new GenericType<List<Object>>() {});
            } else {
                ArrayList<Object> error = new ArrayList<>();
                error.add(Helpers.jsonToMap(Helpers.buildErrorJson("Accommodations service is unreachable.")));

                return error;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            ArrayList<Object> error = new ArrayList<>();
            error.add(Helpers.jsonToMap(Helpers.buildErrorJson("Bookings service is unreachable: " + e.getMessage())));

            return error;
        }
    }

    public List<Object> findAccommodationsFallback(String userId) {
        log.warn("findAccommodationsFallback called");
        return new ArrayList<>();
    }

    @CircuitBreaker
    @Fallback(fallbackMethod = "findBookingsFallback")
    @CommandKey("http-find-bookings")
    @Timeout(value = 1, unit = ChronoUnit.SECONDS)
    @Asynchronous
    public List<Object> findBookings(String userId) {
        try {
            log.info("bookingsUrl = " + bookingsUrl);
            log.info("bookingsUrl.isPresent() = " + bookingsUrl.isPresent());
            log.info("this base url = " + EeConfig.getInstance().getServer().getBaseUrl());

            if (bookingsUrl.isPresent()) {
                return http.target(this.bookingsUrl.get() + "/v1/bookings")
                        .request(MediaType.APPLICATION_JSON)
                        .get(new GenericType<List<Object>>() {});
            } else {
                ArrayList<Object> error = new ArrayList<>();
                error.add(Helpers.jsonToMap(Helpers.buildErrorJson("Bookings service is unreachable.")));

                return error;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            ArrayList<Object> error = new ArrayList<>();
            error.add(Helpers.jsonToMap(Helpers.buildErrorJson("Bookings service is unreachable: " + e.getMessage())));

            return error;
        }
    }

    public List<Object> findBookingsFallback(String userId) {
        log.warn("findBookingsFallback called");
        return new ArrayList<>();
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
