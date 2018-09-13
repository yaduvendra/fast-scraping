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

    public List<DataToExtract> getDataToExtract() {
        return dataToExtract;
    }
}
