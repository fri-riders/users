package com.fri.rso.fririders.users;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("users")
public class UserResource {

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
    public Response createUser(User user) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if (user == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Request body is missing!").build();
        }
        if (user.getEmail() == null || user.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Email or password are required!").build();
        }
        if (UserRepository.findByEmail(user.getEmail()) != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("User with email " + user.getEmail() + " already exists!").build();
        }

        user.setPassword(hashPassword(user.getPassword()));
        user.setUuid(UUID.randomUUID().toString());
        user.setCreatedAt(new Date());

        boolean status = UserRepository.insertUser(user);

        return  status ?
                Response.ok(user).build() :
                Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error has occurred while creating user.").build();
    }

    private static String hashPassword(String rawPassword) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // Generate salt
        final Random random = new SecureRandom();
        byte[] salt = new byte[32];
        random.nextBytes(salt);

        // Hash password
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        messageDigest.update(salt);
        byte[] bytes = messageDigest.digest(rawPassword.getBytes("UTF-8"));
        StringBuilder stringBuilder = new StringBuilder();
        for (byte aByte : bytes) {
            stringBuilder.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }

        return stringBuilder.toString();
    }
}
