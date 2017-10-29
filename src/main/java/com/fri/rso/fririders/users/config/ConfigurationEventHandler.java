package com.fri.rso.fririders.users.config;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import java.util.logging.Logger;

public class ConfigurationEventHandler {

    private static final Logger log = Logger.getLogger(ConfigurationEventHandler.class.getName());

    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
        String watchedKey = "users-config.enable-registration";

        ConfigurationUtil.getInstance().subscribe(watchedKey, (String key, String value) -> {
            if (watchedKey.equals(key)) {
                if ("true".equals(value.toLowerCase())) {
                    log.info("Registration is enabled.");
                } else {
                    log.info("Registration is disabled.");
                }
            }
        });
    }

}
