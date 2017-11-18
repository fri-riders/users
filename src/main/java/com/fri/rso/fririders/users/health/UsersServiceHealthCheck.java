package com.fri.rso.fririders.users.health;

import com.fri.rso.fririders.users.config.ConfigProperties;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Health
@ApplicationScoped
public class UsersServiceHealthCheck implements HealthCheck {

    @Inject
    private ConfigProperties configProperties;

    @Override
    public HealthCheckResponse call() {
        if (configProperties.isHealthy()) {
            return HealthCheckResponse.named(UsersServiceHealthCheck.class.getSimpleName()).up().build();
        } else {
            return HealthCheckResponse.named(UsersServiceHealthCheck.class.getSimpleName()).down().build();
        }
    }

}
