package com.example.workflow.operator.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Very small parser used for examples. It attempts to read a Serverless
 * Workflow JSON definition and maps it to the internal representation used by
 * {@link com.example.workflow.operator.WorkflowRunner}. If JSON parsing fails,
 * it falls back to the previous comma separated format.
 */
public class ServerlessWorkflowParser {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ServerlessWorkflow parse(String definition) {
        try {
            JsonNode root = MAPPER.readTree(definition);
            ServerlessWorkflow wf = new ServerlessWorkflow();
            wf.setId(root.path("id").asText("generated"));
            wf.setVersion(root.path("version").asText("1.0"));
            JsonNode states = root.path("states");
            if (states.isArray()) {
                for (JsonNode state : states) {
                    String name = state.path("name").asText(null);
                    if (name != null && !name.isEmpty()) {
                        wf.getStates().add(new ServerlessState(name));
                    }
                }
            }
            return wf;
        } catch (Exception e) {
            // Fallback for the simple comma separated format used in older
            // examples.
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
}
