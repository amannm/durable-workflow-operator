package com.example.workflow.operator.model;

import java.time.Duration;
import java.util.Map;

public class ServerlessState {
    private final String name;
    private Duration wait;
    private Map<String, String> set;
    // message to log when this state runs
    private String log;
    // URL to fetch via HTTP GET
    private String fetchUrl;
    // name of variable to store fetched result
    private String fetchVar;

    public ServerlessState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Duration getWait() {
        return wait;
    }

    public void setWait(Duration wait) {
        this.wait = wait;
    }

    public Map<String, String> getSet() {
        return set == null ? null : new java.util.HashMap<>(set);
    }

    public void setSet(Map<String, String> set) {
        this.set = set == null ? null : new java.util.HashMap<>(set);
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getFetchUrl() {
        return fetchUrl;
    }

    public void setFetchUrl(String fetchUrl) {
        this.fetchUrl = fetchUrl;
    }

    public String getFetchVar() {
        return fetchVar;
    }

    public void setFetchVar(String fetchVar) {
        this.fetchVar = fetchVar;
    }

    ServerlessState copy() {
        ServerlessState copy = new ServerlessState(name);
        copy.wait = wait;
        copy.set = getSet();
        copy.log = log;
        copy.fetchUrl = fetchUrl;
        copy.fetchVar = fetchVar;
        return copy;
    }
}
