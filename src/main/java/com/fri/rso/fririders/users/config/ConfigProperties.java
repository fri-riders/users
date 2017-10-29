package com.fri.rso.fririders.users.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigBundle("users-config")
public class ConfigProperties {

    @ConfigValue(watch = true)
    private boolean enableRegistration;

    public boolean isEnableRegistration() {
        return enableRegistration;
    }

    public void setEnableRegistration(boolean enableRegistration) {
        this.enableRegistration = enableRegistration;
    }

    public String toJsonString() {
        return String.format(
                "{" +
                    "\"enableRegistration\": \"%b\"" +
                "}",
                this.isEnableRegistration()
        );
    }

}
