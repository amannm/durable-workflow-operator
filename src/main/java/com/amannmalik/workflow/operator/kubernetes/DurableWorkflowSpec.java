package com.amannmalik.workflow.operator.kubernetes;

import io.serverlessworkflow.api.types.Workflow;

public class DurableWorkflowSpec {
    private Workflow definition;

    public Workflow getDefinition() {
        return definition;
    }

    public void setDefinition(Workflow definition) {
        this.definition = definition;
    }
}
