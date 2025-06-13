package com.example.workflow.operator.model;

import java.util.ArrayList;
import java.util.List;

public class ServerlessWorkflow {
    private String id;
    private String version;
    private final List<ServerlessState> states = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<ServerlessState> getStates() {
        return new ArrayList<>(states);
    }

    public void addState(ServerlessState state) {
        states.add(state);
    }

    public ServerlessWorkflow copy() {
        ServerlessWorkflow copy = new ServerlessWorkflow();
        copy.id = id;
        copy.version = version;
        for (ServerlessState s : states) {
            copy.states.add(s.copy());
        }
        return copy;
    }
}
