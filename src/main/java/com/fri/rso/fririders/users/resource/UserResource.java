package com.fri.rso.fririders.users.resource;

import com.fri.rso.fririders.users.config.ConfigProperties;
import com.fri.rso.fririders.users.entity.Jwt;
import com.fri.rso.fririders.users.entity.User;
import com.fri.rso.fririders.users.service.AccommodationsService;
import com.fri.rso.fririders.users.service.AuthService;
import com.fri.rso.fririders.users.service.BookingsService;
import com.fri.rso.fririders.users.service.UsersService;
import com.fri.rso.fririders.users.util.Helpers;
import com.fri.rso.fririders.users.util.PasswordAuthentication;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.util.List;

@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("users")
@Log
public class UserResource {

    private static final Logger log = LogManager.getLogger(UserResource.class.getName());

    @Inject
    private UsersService usersService;

    @Inject
    private AccommodationsService accommodationsService;

    @Inject
    private BookingsService bookingsService;

    @Inject
    private AuthService authService;

    @Inject
    private ConfigProperties configProperties;

    @GET
    @Metered(name = "get_users")
    public Response getUsers() {
        List<User> users = usersService.getUsers();

        return Response.ok(users).build();
    }

    @GET
    @Path("{id}")
    public Response getUser(@Context HttpServletRequest request, @PathParam("id") String id) {
        User user = usersService.findById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(Helpers.buildErrorJson("User not found.")).build();
        }

        String authToken = request.getHeader("authToken");
        if (authToken == null || authToken.equals("")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(Helpers.buildErrorJson("Auth token missing")).build();
        }

        Jwt jwt = new Jwt();
        jwt.setEmail(user.getEmail());
        jwt.setToken(authToken);

        if (!authService.isTokenValid(jwt)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(Helpers.buildErrorJson("Invalid token.")).build();
        }

        return Response.ok(user).build();
    }

    @PUT
    @Path("{id}")
    public Response updateUser(@Context HttpServletRequest request, @PathParam("id") String id, User updatedUser) {
        User user = usersService.findById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(Helpers.buildErrorJson("User not found.")).build();
        }

        String authToken = request.getHeader("authToken");
        if (authToken == null || authToken.equals("")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(Helpers.buildErrorJson("Auth token missing")).build();
        }

        Jwt jwt = new Jwt();
        jwt.setEmail(user.getEmail());
        jwt.setToken(authToken);

        if (!authService.isTokenValid(jwt)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(Helpers.buildErrorJson("Invalid token.")).build();
        }

        User result = usersService.updateUser(user, updatedUser);

        return result != null ? Response.ok(result).build() : Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteUser(@Context HttpServletRequest request, @PathParam("id") String id) {
        User user = usersService.findById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(Helpers.buildErrorJson("User not found.")).build();
        }

        String authToken = request.getHeader("authToken");
        if (authToken == null || authToken.equals("")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(Helpers.buildErrorJson("Auth token missing")).build();
        }

        Jwt jwt = new Jwt();
        jwt.setEmail(user.getEmail());
        jwt.setToken(authToken);

        if (!authService.isTokenValid(jwt)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(Helpers.buildErrorJson("Invalid token.")).build();
        }

        return (usersService.deleteUser(id) ? Response.ok() : Response.status(Response.Status.INTERNAL_SERVER_ERROR)).build();
    }

    @POST
    @Metered(name = "register")
    public Response register(User user) {
        if (!configProperties.isEnableRegistration()) {
            return Response.status(Response.Status.FORBIDDEN).entity(Helpers.buildErrorJson("Registration is currently disabled, please try again later.")).build();
        }
        if (user == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Helpers.buildErrorJson("Request body is missing!")).build();
        }
        if (user.getEmail() == null || user.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Helpers.buildErrorJson("Email and password are required!")).build();
        }
        if (usersService.findByEmail(user.getEmail()) != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Helpers.buildErrorJson("User with email " + user.getEmail() + " already exists!")).build();
        }
        if (configProperties.getPasswordMinLength() > user.getPassword().length()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Helpers.buildErrorJson("Password is too short! It should be at least " + configProperties.getPasswordMinLength() + " characters long.")).build();
        }

        User createdUser = usersService.createUser(user);

        if (createdUser != null) {
            Jwt jwt = authService.getJwtForUser(user.getEmail());

            if (jwt != null) {
                return Response.ok(jwt).build();
            } else {
                return Response.ok(Helpers.buildMessageJson("Logging in automatically failed. Please navigate to login to continue")).build();
            }
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Helpers.buildErrorJson("An error has occurred while creating user.")).build();
    }

    @GET
    @Path("login")
    @Metered(name = "login")
    public Response login(@QueryParam("email") String email, @QueryParam("password") String password) {
        if (!configProperties.isEnableLogin()) {
            return Response.status(Response.Status.FORBIDDEN).entity(Helpers.buildErrorJson("Login is currently disabled, please try again later.")).build();
        }

        log.info("login user with email = " + email);

        User user = usersService.findByEmail(email);

        if (user == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Helpers.buildErrorJson("User not found.")).build();
        }

        if (new PasswordAuthentication().authenticate(password.toCharArray(), user.getPassword())) {
            Jwt jwt = authService.getJwtForUser(user.getEmail());

            if (jwt != null) {
                log.info("received JWT for user " + jwt.getEmail());

                return Response.ok(jwt).build();
            } else {
                log.warn("FAILED to received JWT for user");

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Helpers.buildErrorJson("Login failed. Please try again,")).build();
            }
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity(Helpers.buildErrorJson("Login failed. Make sure that you have entered the right email-password combination.")).build();
        }
    }

    @GET
    @Path("{id}/accommodations")
    @Timed(name = "get_accommodations_for_user")
    public Response getAccommodationsForUser(@PathParam("id") String id) {
        User user = usersService.findById(id);

        if (user == null) {
            log.warn("User " + id + " not found");

            return Response.status(Response.Status.NOT_FOUND).entity(Helpers.buildErrorJson("User not found.")).build();
        }

        return Response.ok(accommodationsService.findAccommodationsForUser(id)).build();
    }

    @GET
    @Path("{id}/bookings")
    @Timed(name = "get_bookings_for_user")
    public Response getBookingsForUser(@PathParam("id") String id) {
        User user = usersService.findById(id);

        if (user == null) {
            log.warn("User " + id + " not found");

            return Response.status(Response.Status.NOT_FOUND).entity(Helpers.buildErrorJson("User not found.")).build();
        }

        return Response.ok(bookingsService.findBookingsForUser(id)).build();
    }
}
