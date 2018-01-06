package com.fri.rso.fririders.users.service;

import com.fri.rso.fririders.users.entity.Jwt;
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
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@RequestScoped
@Log
public class AuthService {

    private static final Logger log = LogManager.getLogger(AuthService.class.getName());

    private Client http = ClientBuilder.newClient();

    @Inject
    @DiscoverService(value = "auth", version = "*", environment = "dev", accessType = AccessType.DIRECT)
    private Optional<String> authUrl;

    @CircuitBreaker
    @Fallback(fallbackMethod = "getJwtForUserFallback")
    @CommandKey("http-auth-issue-jwt")
    @Timeout(value = 5, unit = ChronoUnit.SECONDS)
    @Asynchronous
    public Jwt getJwtForUser(String email) {
        if (this.authUrl.isPresent()) {
            log.info("Call auth service: issue");
            log.info("URL: " + this.authUrl.get() + "/v1/auth/issue");

            Jwt jwt = new Jwt();
            jwt.setEmail(email);

            return http.target(this.authUrl.get() + "/v1/auth/issue")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(jwt, MediaType.APPLICATION_JSON), Jwt.class);
        } else {
            log.warn("Auth service URL not available");

            return null;
        }
    }

    public String getJwtForUserFallback() {
        log.warn("Auth service URL not available (getJwtForUserFallback invoked)");
        return null;
    }

    @CircuitBreaker
    @Fallback(fallbackMethod = "isTokenValidFallback")
    @CommandKey("http-auth-verify-jwt")
    @Timeout(value = 5, unit = ChronoUnit.SECONDS)
    @Asynchronous
    public boolean isTokenValid(Jwt jwt) {
        try {
            if (this.authUrl.isPresent()) {
                log.info("Call auth service: verify");
                log.info("URL: " + this.authUrl.get() + "/v1/auth/verify");

                Response response = http.target(this.authUrl.get() + "/v1/auth/verify")
                        .request(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(jwt, MediaType.APPLICATION_JSON), Response.class);

                return response.getStatus() == 200;
            } else {
                log.warn("Auth service URL not available");

                return false;
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            return false;
        }
    }

    public boolean isTokenValidFallback() {
        log.warn("Auth service URL not available (isTokenValidFallback invoked)");
        return false;
    }

}
