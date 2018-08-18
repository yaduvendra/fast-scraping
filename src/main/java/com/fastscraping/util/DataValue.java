package com.fastscraping.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

@JsonDeserialize(using = DataValue.DataValueDeserializer.class)
public interface DataValue<T> {
    T get();
    boolean equals(DataValue compareWith);

    public class NullValue<T> implements DataValue{
        @Override
        public T get() {
            return null;
        }

        @Override
        public boolean equals(DataValue compareWith) {
            return this.get() == null && this.get() == compareWith.get();
        }

        @Override
        public String toString() {
            return "";
        }
    }

    public class NonNullValue<T> implements DataValue {
        private final T value;

        @JsonCreator
        public NonNullValue(@JsonProperty("value") T value) throws NullValueException {
            if(value != null) {
                this.value = value;
            } else {
                throw new NullValueException("The NonNullValue can't be initialized with null.");
            }
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public String toString(){
            return value.toString();
        }

        @Override
        public boolean equals(DataValue compareWith) {
            return this.get() == compareWith.get();
        }
    }

    public class DataValueDeserializer<T> extends JsonDeserializer<DataValue<T>> {

        @Override
        public DataValue<T> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            ObjectCodec mapper = p.getCodec();
            JsonNode root = mapper.readTree(p);

            root.fieldNames().forEachRemaining(System.out::println);
            
            if(root.fieldNames().next() == "value"){
                return (DataValue<T>) new NonNullValue(root.get("value"));
            } else {
                return new NullValue();
            }
        }
    }
}

