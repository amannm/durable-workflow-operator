package com.example.workflow.operator;

import com.example.workflow.operator.model.ServerlessWorkflow;

public class WorkflowResourceSpec {
    private ServerlessWorkflow definition;

    public ServerlessWorkflow getDefinition() {
        return definition;
    }

    public void setDefinition(ServerlessWorkflow definition) {
        this.definition = definition;
    }
}
