package com.fri.rso.fririders.users.config;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import java.util.logging.Logger;

public class ConfigurationEventHandler {

    private static final Logger log = Logger.getLogger(ConfigurationEventHandler.class.getName());

    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
        String enableRegistrationWatchedKey = "users-config.enable-registration";
        String enableLoginWatchedKey = "users-config.enable-login";

        ConfigurationUtil.getInstance().subscribe(enableRegistrationWatchedKey, (String key, String value) -> {
            if (enableRegistrationWatchedKey.equals(key)) {
                if ("true".equals(value.toLowerCase())) {
                    log.info("Registration is enabled.");
                } else {
                    log.info("Registration is disabled.");
                }
            }
        });

        ConfigurationUtil.getInstance().subscribe(enableLoginWatchedKey, (String key, String value) -> {
            if (enableLoginWatchedKey.equals(key)) {
                if ("true".equals(value.toLowerCase())) {
                    log.info("Login is enabled.");
                } else {
                    log.info("Login is disabled.");
                }
            }
        });
    }

}
