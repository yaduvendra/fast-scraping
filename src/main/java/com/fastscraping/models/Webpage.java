package com.fastscraping.models;

import com.fastscraping.util.DataValue;

public class Webpage {

    private final DataValue urlRegex;
    private final DataValue uniqueH1;
    private final DataValue uniqueH2;
    private final DataValue uniqueStringRegex;
    private final ElementWithActions elementWithActions;

    private Webpage(DataValue urlRegex, DataValue uniqueH1, DataValue uniqueH2,
                    DataValue uniqueStringRegex, ElementWithActions elementWithActions) {
        this.urlRegex = urlRegex;
        this.uniqueH1 = uniqueH1;
        this.uniqueH2 = uniqueH2;
        this.uniqueStringRegex = uniqueStringRegex;
        this.elementWithActions = elementWithActions;
    }

    public static class WebpageBuilder {
        private DataValue urlRegex;
        private DataValue uniqueH1;
        private DataValue uniqueH2;
        private DataValue uniqueStringRegex;
        private ElementWithActions elementWithActions;

        public WebpageBuilder() {

        }

        public WebpageBuilder setURLRegex(DataValue urlRegex) {
            this.urlRegex = urlRegex;
            return this;
        }

        public WebpageBuilder setUniqueH1(DataValue uniqueH1) {
            this.uniqueH1 = uniqueH1;
            return this;
        }

        public WebpageBuilder setUniqueH2(DataValue uniqueH2) {
            this.uniqueH2 = uniqueH2;
            return this;
        }

        public WebpageBuilder setUniqueStringRegex(DataValue uniqueStringRegex) {
            this.uniqueStringRegex = uniqueStringRegex;
            return this;
        }

        public WebpageBuilder setElementWithActions(ElementWithActions elementWithActions) {
            this.elementWithActions = elementWithActions;
            return this;
        }

        public Webpage build() {
            return new Webpage(this.urlRegex,
                    this.uniqueH1,
                    this.uniqueH2,
                    this.uniqueStringRegex,
                    this.elementWithActions);
        }
    }
}
