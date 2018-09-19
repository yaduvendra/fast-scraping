package com.fastscraping.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastscraping.models.ScrapingInformation;
import com.fastscraping.models.WebpageDetails;

import java.io.IOException;
import java.util.List;

public class JsonHelper {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(DeserializerRegistry.getModule());
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public static synchronized String toJsonString(Object object) {
        try {
            return objectMapper.writer().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static synchronized String toPrettyJsonString(Object object) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static synchronized List<WebpageDetails> getWebpageDetailsFromJson(
            String jsonString, TypeReference<List<WebpageDetails>> className) throws IOException {
        return objectMapper.readValue(jsonString, className);
    }

    public static synchronized ScrapingInformation getScrapingInformationFromJson(
            String jsonString, TypeReference<ScrapingInformation> className) throws IOException {
        return objectMapper.readValue(jsonString, className);
    }
}
