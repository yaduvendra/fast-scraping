package com.fastscraping.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fastscraping.util.DataValue;

import static com.fastscraping.models.HTMLTag.HTMLTagWithText;

public class Webpage {

    private final DataValue<String> urlRegex;
    private final DataValue<HTMLTagWithText> uniqueTag;
    private final DataValue<String> uniqueStringRegex;
    private final ElementWithActions elementWithActions;

    @JsonCreator
    public Webpage(@JsonProperty("urlRegex") DataValue<String> urlRegex,
                   @JsonProperty("uniqueTag") DataValue<HTMLTagWithText> uniqueTag,
                   @JsonProperty("uniqueStringRegex") DataValue<String> uniqueStringRegex,
                   @JsonProperty("elementWithActions") ElementWithActions elementWithActions) {
        this.urlRegex = urlRegex;
        this.uniqueTag = uniqueTag;
        this.uniqueStringRegex = uniqueStringRegex;
        this.elementWithActions = elementWithActions;
    }

    public static class WebpageBuilder {
        private DataValue<String> urlRegex;
        private DataValue<HTMLTagWithText> uniqueTag;
        private DataValue<String> uniqueStringRegex;
        private ElementWithActions elementWithActions;

        public WebpageBuilder() {

        }

        public WebpageBuilder setURLRegex(DataValue<String> urlRegex) {
            this.urlRegex = urlRegex;
            return this;
        }


        public WebpageBuilder setUniqueTag(DataValue<HTMLTagWithText> uniqueTag) {
            this.uniqueTag = uniqueTag;
            return this;
        }

        public WebpageBuilder setUniqueStringRegex(DataValue<String> uniqueStringRegex) {
            this.uniqueStringRegex = uniqueStringRegex;
            return this;
        }

        public WebpageBuilder setElementWithActions(ElementWithActions elementWithActions) {
            this.elementWithActions = elementWithActions;
            return this;
        }

        public Webpage build() {
            return new Webpage(this.urlRegex, this.uniqueTag, this.uniqueStringRegex, this.elementWithActions);
        }
    }
}
