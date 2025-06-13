package com.example.workflow.operator.model;

import java.time.Duration;

public class ServerlessState {
    private final String name;
    private Duration wait;

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
}
