package com.fri.rso.fririders.users;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("users")
public class UserResource {

    @GET
    public Response getUsers() {
        List<User> users = Database.getUsers();

        return Response.ok(users).build();
    }

    @GET
    @Path("{userId}")
    public Response getUser(@PathParam("userId") String userId) {
        User user = Database.getUser(userId);

        return (user != null ? Response.ok(user) : Response.status(Response.Status.NOT_FOUND)).build();
    }

}
