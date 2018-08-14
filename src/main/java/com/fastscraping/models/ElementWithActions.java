package com.fastscraping.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ElementWithActions {

    private String selector;
    private List<ActionName> actions;

    @JsonCreator
    public ElementWithActions(@JsonProperty("selector") String selector, @JsonProperty("actions")List<ActionName> actions) {
        this.selector = selector;
        this.actions = actions;
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

        public ElementWithActionsBuilder setSelector(String selector) {
            this.selector = selector;
            return this;
        }

        public ElementWithActionsBuilder setActions(List<ActionName> actions) {
            this.actions = actions;
            return this;
        }

        public ElementWithActions build() {
            return new ElementWithActions(selector, actions);
        }
    }
}
