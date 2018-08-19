package com.fastscraping.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonHelper {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(DeserializerRegistry.getModule());
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

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
