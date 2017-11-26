package com.fri.rso.fririders.users.resource;

import com.fri.rso.fririders.users.config.ConfigProperties;
import com.kumuluz.ee.logs.cdi.Log;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("config")
@Log
public class ConfigResource {

    @Inject
    private ConfigProperties configProperties;

    @GET
    public Response getConfig() {
        return Response.ok(configProperties.toJsonString()).build();
    }
}
