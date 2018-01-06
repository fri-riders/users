package com.fri.rso.fririders.users.service;

import com.fri.rso.fririders.users.util.Helpers;
import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.discovery.enums.AccessType;
import com.kumuluz.ee.fault.tolerance.annotations.CommandKey;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
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
public class AccommodationsService {

    private static final Logger log = LogManager.getLogger(AccommodationsService.class.getName());

    private Client http = ClientBuilder.newClient();

    @Inject
    @DiscoverService(value = "accommodations", version = "*", environment = "dev", accessType = AccessType.DIRECT)
    private Optional<String> accommodationsUrl;

    @CircuitBreaker
    @Fallback(fallbackMethod = "findAccommodationsFallback")
    @CommandKey("http-find-accommodations")
    @Timeout(value = 5, unit = ChronoUnit.SECONDS)
    @Asynchronous
    public List<Object> findAccommodationsForUser(String userId) {
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
            log.error(e.getMessage());

            ArrayList<Object> error = new ArrayList<>();
            error.add(Helpers.jsonToMap(Helpers.buildErrorJson("Bookings service is unreachable: " + e.getMessage())));

            return error;
        }
    }

    public List<Object> findAccommodationsFallback(String userId) {
        log.warn("findAccommodationsFallback called");
        return new ArrayList<>();
    }

}
