package com.fri.rso.fririders.users;

import com.kumuluz.ee.discovery.annotations.RegisterService;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@RegisterService(value = "users", ttl = 20, pingInterval = 15, environment = "dev", version = "1.0.0", singleton = false)
@ApplicationPath("v1")
public class UserApplication extends Application {
}
