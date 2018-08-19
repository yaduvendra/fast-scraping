package com.fastscraping.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static com.fastscraping.models.HTMLTag.HTMLTagWithText;

public class Webpage {

    private final String urlRegex;
    private final HTMLTagWithText uniqueTag;
    private final String uniqueStringRegex;
    private final List<ElementWithActions> elementWithActions;

    @JsonCreator
    public Webpage(@JsonProperty("urlRegex") String urlRegex,
                   @JsonProperty("uniqueTag") HTMLTagWithText uniqueTag,
                   @JsonProperty("uniqueStringRegex") String uniqueStringRegex,
                   @JsonProperty("elementWithActions") List<ElementWithActions> elementWithActions) {
        this.urlRegex = urlRegex;
        this.uniqueTag = uniqueTag;
        this.uniqueStringRegex = uniqueStringRegex;
        this.elementWithActions = elementWithActions;
    }

    public static class WebpageBuilder {
        private String urlRegex;
        private HTMLTagWithText uniqueTag;
        private String uniqueStringRegex;
        private List<ElementWithActions> elementWithActions;

        public WebpageBuilder() {

        }

        public WebpageBuilder setURLRegex(String urlRegex) {
            this.urlRegex = urlRegex;
            return this;
        }


        public WebpageBuilder setUniqueTag(HTMLTagWithText uniqueTag) {
            this.uniqueTag = uniqueTag;
            return this;
        }

        public WebpageBuilder setUniqueStringRegex(String uniqueStringRegex) {
            this.uniqueStringRegex = uniqueStringRegex;
            return this;
        }

        public WebpageBuilder setElementWithActions(List<ElementWithActions> elementWithActions) {
            this.elementWithActions = elementWithActions;
            return this;
        }

        public Webpage build() {
            return new Webpage(this.urlRegex, this.uniqueTag, this.uniqueStringRegex, this.elementWithActions);
        }
    }
}
