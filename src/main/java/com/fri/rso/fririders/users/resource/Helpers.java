package com.fri.rso.fririders.users.resource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;

public class Helpers {

    /**
     * @param message Error message
     * @return Json formatted string
     */
    public static String buildErrorJson(String message) {
        return "{ " +
                "\"error\": true," +
                "\"message\": \"" + message + "\"" +
                " }";
    }

    /**
     * @param jsonString String to parse
     * @return HashMap in JSON form
     */
    public static HashMap<String, String> jsonToMap(String jsonString) {
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

    /**
     * @param message Message to send
     * @return JSON object with message
     */
    public static String buildMessageJson(String message) {
        return "{ " +
                "\"message\": \"" + message + "\"" +
                " }";
    }

}
