package com.fri.rso.fririders.users;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("users")
public class UserResource {

    private static Client client = ClientBuilder.newClient();
    private static String accommodationsEndpoint = "http://localhost:8085/accommodations/all";

    @GET
    public Response getUsers() {
        List<User> users = UserRepository.getUsers();

        return Response.ok(users).build();
    }

    @GET
    @Path("{userUuid}")
    public Response getUser(@PathParam("userUuid") String userUuid) {
        User user = UserRepository.findByUuid(userUuid);

        return (user != null ? Response.ok(user) : Response.status(Response.Status.NOT_FOUND)).build();
    }

    @POST
    public Response createUser(User user) {
        if (user == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Request body is missing!").build();
        }
        if (user.getEmail() == null || user.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Email or password are required!").build();
        }
        if (UserRepository.findByEmail(user.getEmail()) != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("User with email " + user.getEmail() + " already exists!").build();
        }

        return  UserRepository.insertUser(user) ?
                Response.ok(user).build() :
                Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error has occurred while creating user.").build();
    }

    @GET
    @Path("{userUuid}/accommodations")
    public Response getAccommodationsForUser(@PathParam("userUuid") String uuid) {
        User user = UserRepository.findByUuid(uuid);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User with uuid " + uuid + " not found.").build();
        }

        List<Object> accommodations = client
                .target(accommodationsEndpoint)
                .request(MediaType.APPLICATION_JSON)
                .get((new GenericType<List<Object>>() {}));

        return Response.ok(accommodations).build();
    }

    private static HashMap<String, String> jsonToMap(String jsonString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readValue(jsonString, new TypeReference<HashMap<String, String>>() {});
        } catch (JsonParseException | JsonMappingException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
