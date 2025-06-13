package com.example.workflow.operator.model;

import io.serverlessworkflow.api.WorkflowFormat;
import io.serverlessworkflow.api.WorkflowReader;
import io.serverlessworkflow.api.types.Workflow;
import java.io.IOException;

/** Utility class to parse workflow definitions in YAML format. */
public final class WorkflowParser {
    private WorkflowParser() {}

    /**
     * Parse the given YAML string into the internal {@link ServerlessWorkflow} model.
     * Only the workflow id (mapped from document name) and version are extracted.
     */
    public static ServerlessWorkflow parseYaml(String yaml) throws IOException {
        Workflow wf = WorkflowReader.readWorkflowFromString(yaml, WorkflowFormat.YAML);
        ServerlessWorkflow result = new ServerlessWorkflow();
        if (wf.getDocument() != null) {
            result.setId(wf.getDocument().getName());
            result.setVersion(wf.getDocument().getVersion());
        }
        return result;
    }
}
