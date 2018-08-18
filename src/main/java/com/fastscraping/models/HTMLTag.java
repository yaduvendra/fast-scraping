package com.fastscraping.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public interface HTMLTag {

    public static class HTMLTagWithText implements HTMLTag {
        private final String selector;
        private final String text;

        @JsonCreator
        public HTMLTagWithText(@JsonProperty("name") String name, @JsonProperty("text")  String text) {
            this.selector = name;
            this.text = text;
        }
    }

}
