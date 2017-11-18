package com.fri.rso.fririders.users.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigBundle("users-config")
public class ConfigProperties {

    @ConfigValue(watch = true)
    private boolean enableRegistration;

    @ConfigValue(watch = true)
    private boolean enableLogin;

    private int passwordMinLength;

    private boolean healthy;

    public String toJsonString() {
        return String.format(
                "{" +
                        "\"enableRegistration\": %b," +
                        "\"enableLogin\": %b," +
                        "\"passwordMinLength\": %d," +
                        "\"healthy\": %b" +
                        "}",
                this.isEnableRegistration(),
                this.isEnableLogin(),
                this.getPasswordMinLength(),
                this.isHealthy()
        );
    }

    public boolean isEnableRegistration() {
        return enableRegistration;
    }

    public void setEnableRegistration(boolean enableRegistration) {
        this.enableRegistration = enableRegistration;
    }

    public boolean isEnableLogin() {
        return enableLogin;
    }

    public void setEnableLogin(boolean enableLogin) {
        this.enableLogin = enableLogin;
    }

    public int getPasswordMinLength() {
        return passwordMinLength;
    }

    public void setPasswordMinLength(int passwordMinLength) {
        this.passwordMinLength = passwordMinLength;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }
}
