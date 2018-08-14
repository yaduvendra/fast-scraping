package com.fastscraping.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastscraping.models.ElementWithActions;

import java.io.IOException;
import java.util.List;

public class JsonHelper {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static synchronized String toJsonString(Object object) throws JsonProcessingException {
        return objectMapper.writer().writeValueAsString(object);
    }

    public static synchronized String toPrettyJsonString(Object object) throws JsonProcessingException {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }

    public static synchronized <T> T getObjectFromJson(String jsonString, Class<T> className) throws IOException {
        return objectMapper.readValue(jsonString, className);
    }
}
