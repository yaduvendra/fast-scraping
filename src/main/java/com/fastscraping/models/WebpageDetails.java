package com.fastscraping.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static com.fastscraping.models.HTMLTag.HTMLTagWithText;

public class WebpageDetails {

    private final String urlRegex;
    private final HTMLTagWithText uniqueTag;
    private final String uniqueStringOnPage;
    private final List<ActionsAndData> actionsAndData;

    @JsonCreator
    public WebpageDetails(@JsonProperty("urlRegex") String urlRegex,
                          @JsonProperty("uniqueTag") HTMLTagWithText uniqueTag,
                          @JsonProperty("uniqueStringOnPage") String uniqueStringOnPage,
                          @JsonProperty("actionsAndData") List<ActionsAndData> actionsAndData) {
        this.urlRegex = urlRegex;
        this.uniqueTag = uniqueTag;
        this.uniqueStringOnPage = uniqueStringOnPage;
        this.actionsAndData = actionsAndData;
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

    public List<ActionsAndData> getActionsAndData() {
        return actionsAndData;
    }

    public static class WebpageBuilder {
        private String urlRegex;
        private HTMLTagWithText uniqueTag;
        private String uniqueStringOnPage;
        private List<ActionsAndData> actionsAndData;

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

        public WebpageBuilder setActionAndData(List<ActionsAndData> actionsAndData) {
            this.actionsAndData = actionsAndData;
            return this;
        }

        public WebpageDetails build() {
            return new WebpageDetails(this.urlRegex, this.uniqueTag, this.uniqueStringOnPage, this.actionsAndData);
        }
    }
}
