package com.fastscraping.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static com.fastscraping.models.HTMLTag.HTMLTagWithText;

public class Webpage {

    private final String urlRegex;
    private final HTMLTagWithText uniqueTag;
    private final String uniqueStringOnPage;
    private final List<ElementWithActions> elementWithActions;

    @JsonCreator
    public Webpage(@JsonProperty("urlRegex") String urlRegex,
                   @JsonProperty("uniqueTag") HTMLTagWithText uniqueTag,
                   @JsonProperty("uniqueStringOnPage") String uniqueStringOnPage,
                   @JsonProperty("elementWithActions") List<ElementWithActions> elementWithActions) {
        this.urlRegex = urlRegex;
        this.uniqueTag = uniqueTag;
        this.uniqueStringOnPage = uniqueStringOnPage;
        this.elementWithActions = elementWithActions;
    }

    public String getUrlRegex() {
        return urlRegex;
    }

    public HTMLTagWithText getUniqueTag() {
        return uniqueTag;
    }

    public String getUniqueStringOnPage() {
        return uniqueStringOnPage;
    }

    public List<ElementWithActions> getElementWithActions() {
        return elementWithActions;
    }

    public static class WebpageBuilder {
        private String urlRegex;
        private HTMLTagWithText uniqueTag;
        private String uniqueStringOnPage;
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

        public WebpageBuilder setUniqueStringOnPage(String uniqueStringOnPage) {
            this.uniqueStringOnPage = uniqueStringOnPage;
            return this;
        }

        public WebpageBuilder setElementWithActions(List<ElementWithActions> elementWithActions) {
            this.elementWithActions = elementWithActions;
            return this;
        }

        public Webpage build() {
            return new Webpage(this.urlRegex, this.uniqueTag, this.uniqueStringOnPage, this.elementWithActions);
        }
    }
}
