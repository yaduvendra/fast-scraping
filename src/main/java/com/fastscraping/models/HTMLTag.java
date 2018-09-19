package com.fastscraping.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public interface HTMLTag {

    @JsonIgnoreProperties(ignoreUnknown = true)
    class HTMLTagWithText implements HTMLTag {
        private final String selector;
        private final String text;

        @JsonCreator
        public HTMLTagWithText(@JsonProperty("selector") String selector, @JsonProperty("text")  String text) {
            this.selector = selector;
            this.text = text;
        }

        public String getSelector() {
            return selector;
        }

        public String getText() {
            return text;
        }

        public boolean isNotNull() {
            return selector != null && selector.trim() != "" && text != null;
        }
    }

}
