package com.fastscraping.dao.mongo;

import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;

public class MongoConfig {
    //TODO: Move the mongo related config here

    public static JsonWriterSettings settings = JsonWriterSettings.builder().outputMode(JsonMode.EXTENDED).build();
}
