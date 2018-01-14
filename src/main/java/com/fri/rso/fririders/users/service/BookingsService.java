package com.fri.rso.fririders.users.service;

import com.fri.rso.fririders.users.util.Helpers;
import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.discovery.enums.AccessType;
import com.kumuluz.ee.fault.tolerance.annotations.CommandKey;
import com.kumuluz.ee.fault.tolerance.annotations.GroupKey;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.faulttolerance.*;

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
@Bulkhead
@GroupKey("users")
public class BookingsService {

    private static final Logger log = LogManager.getLogger(BookingsService.class.getName());

    private Client http = ClientBuilder.newClient();

    @Inject
    @DiscoverService(value = "display-bookings", version = "*", environment = "dev", accessType = AccessType.DIRECT)
    private Optional<String> bookingsUrl;

    @CircuitBreaker(requestVolumeThreshold = 2)
    @Fallback(fallbackMethod = "findBookingsFallback")
    @CommandKey("http-find-bookings")
    @Timeout(value = 5, unit = ChronoUnit.SECONDS)
    @Asynchronous
    public List<Object> findBookingsForUser(String userId) {
        try {
            log.info("bookingsUrl = " + bookingsUrl);
            log.info("bookingsUrl.isPresent() = " + bookingsUrl.isPresent());

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
            log.error(e.getMessage());

            ArrayList<Object> error = new ArrayList<>();
            error.add(Helpers.jsonToMap(Helpers.buildErrorJson("Bookings service is unreachable: " + e.getMessage())));

            return error;
        }
    }

    public List<Object> findBookingsFallback(String userId) {
        log.warn("findBookingsFallback called");
        return new ArrayList<>();
    }

}
