package com.example.workflow.operator.model;

import java.time.Duration;
import java.util.Map;

public class ServerlessState {
    private final String name;
    private Duration wait;
    private Map<String, String> set;

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

    ServerlessState copy() {
        ServerlessState copy = new ServerlessState(name);
        copy.wait = wait;
        copy.set = getSet();
        return copy;
    }
}
