package com.example.workflow.operator.model;

public class ServerlessWorkflowParser {
    public static ServerlessWorkflow parse(String definition) {
        ServerlessWorkflow wf = new ServerlessWorkflow();
        wf.setId("generated");
        wf.setVersion("1.0");
        for (String part : definition.split(",")) {
            String name = part.trim();
            if (!name.isEmpty()) {
                wf.getStates().add(new ServerlessState(name));
            }
        }
        return wf;
    }
}
