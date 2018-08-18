package com.fastscraping.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ElementWithActions {

    private final String selector;
    private final List<ActionName> actions;
    private final List<DataToExtract> dataToExtract;


    @JsonCreator
    public ElementWithActions(@JsonProperty("selector") String selector,
                              @JsonProperty("actions") List<ActionName> actions,
                              @JsonProperty("dataToExtract") List<DataToExtract> dataToExtract
                              ) {
        this.selector = selector;
        this.actions = actions;
        this.dataToExtract = dataToExtract;
    }

    public String getSelector() {
        return selector;
    }

    public List<ActionName> getActions() {
        return actions;
    }


    /**
     * The builder of the ElementWithActions class.
     */
    public static class ElementWithActionsBuilder {
        private String selector;
        private List<ActionName> actions;
        private List<DataToExtract> dataToExtract;

        public ElementWithActionsBuilder setSelector(String selector) {
            this.selector = selector;
            return this;
        }

        public ElementWithActionsBuilder setActions(List<ActionName> actions) {
            this.actions = actions;
            return this;
        }

        public ElementWithActionsBuilder setDataToExtract(List<DataToExtract> dataToExtract) {
            this.dataToExtract = dataToExtract;
            return this;
        }

        public ElementWithActions build() {
            return new ElementWithActions(selector, actions, dataToExtract);
        }
    }
}
