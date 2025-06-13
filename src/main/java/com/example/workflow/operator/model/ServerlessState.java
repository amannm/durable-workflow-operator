package com.example.workflow.operator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    // URL to invoke via HTTP POST
    private String postUrl;
    // POST request body
    private String postBody;
    // name of variable to store POST response
    private String postVar;
    // variables to remove from the workflow data
    private java.util.List<String> unset;

    @JsonCreator
    public ServerlessState(@JsonProperty("name") String name) {
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

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public String getPostBody() {
        return postBody;
    }

    public void setPostBody(String postBody) {
        this.postBody = postBody;
    }

    public String getPostVar() {
        return postVar;
    }

    public void setPostVar(String postVar) {
        this.postVar = postVar;
    }

    public java.util.List<String> getUnset() {
        return unset == null ? null : new java.util.ArrayList<>(unset);
    }

    public void setUnset(java.util.List<String> unset) {
        this.unset = unset == null ? null : new java.util.ArrayList<>(unset);
    }

    ServerlessState copy() {
        ServerlessState copy = new ServerlessState(name);
        copy.wait = wait;
        copy.set = getSet();
        copy.log = log;
        copy.fetchUrl = fetchUrl;
        copy.fetchVar = fetchVar;
        copy.postUrl = postUrl;
        copy.postBody = postBody;
        copy.postVar = postVar;
        copy.unset = getUnset();
        return copy;
    }
}
