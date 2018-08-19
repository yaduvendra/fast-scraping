package com.fastscraping.util;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;


public class DeserializerRegistry {

    private static SimpleModule module = new SimpleModule("PolymorphicAnimalDeserializerModule",
            new Version(1, 0, 0, null));


    public static SimpleModule getModule() {
        return module;
    }
}
