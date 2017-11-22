package com.fri.rso.fririders.users.resource;

import com.fri.rso.fririders.users.config.ConfigProperties;
import com.fri.rso.fririders.users.entity.User;
import com.fri.rso.fririders.users.service.UserService;
import com.kumuluz.ee.logs.cdi.Log;
import com.kumuluz.ee.logs.cdi.LogParams;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("users")
@Log(LogParams.METRICS)
public class UserResource {

    @Inject
    private UserService usersBean;

    @Inject
    private ConfigProperties configProperties;

    @GET
    @Metered(name = "get_users")
    public Response getUsers() {
        List<User> users = usersBean.getUsers();

        return Response.ok(users).build();
    }

    @GET
    @Path("{id}")
    public Response getUser(@PathParam("id") String id) {
        User user = usersBean.findById(id);

        return (user != null ? Response.ok(user) : Response.status(Response.Status.NOT_FOUND)).build();
    }

    @POST
    @Metered(name = "create_user")
    public Response createUser(User user) {
        if (!configProperties.isEnableRegistration()) {
            return Response.status(Response.Status.FORBIDDEN).entity(Helpers.buildErrorJson("Registration is currently disabled, please try again later.")).build();
        }
        if (user == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Helpers.buildErrorJson("Request body is missing!")).build();
        }
        if (user.getEmail() == null || user.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Helpers.buildErrorJson("Email and password are required!")).build();
        }
        if (usersBean.findByEmail(user.getEmail()) != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Helpers.buildErrorJson("User with email " + user.getEmail() + " already exists!")).build();
        }
        if (configProperties.getPasswordMinLength() > user.getPassword().length()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Helpers.buildErrorJson("Password is too short! It should be at least " + configProperties.getPasswordMinLength() + " characters long.")).build();
        }

        return  usersBean.createUser(user) ?
                Response.ok(user).build() :
                Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Helpers.buildErrorJson("An error has occurred while creating user.")).build();
    }

    @GET
    @Path("{id}/accommodations")
    @Timed(name = "get_accommodations_for_user")
    public Response getAccommodationsForUser(@PathParam("id") String id) {
        User user = usersBean.findById(id);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(Helpers.buildErrorJson("User with uuid " + id + " not found.")).build();
        }

        return Response.ok(usersBean.findAccommodations(id)).build();
    }
}
