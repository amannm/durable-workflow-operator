package com.example.workflow.operator.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;

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
                        ServerlessState s = new ServerlessState(name);
                        JsonNode waitNode = state.get("wait");
                        if (waitNode != null && !waitNode.isNull()) {
                            s.setWait(parseDuration(waitNode));
                        }
                        wf.getStates().add(s);
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

    private static Duration parseDuration(JsonNode node) {
        if (node.isTextual()) {
            try {
                return Duration.parse(node.asText());
            } catch (Exception e) {
                return null;
            }
        }
        if (node.isObject()) {
            long days = node.path("days").asLong(0);
            long hours = node.path("hours").asLong(0);
            long minutes = node.path("minutes").asLong(0);
            long seconds = node.path("seconds").asLong(0);
            long millis = node.path("milliseconds").asLong(0);
            Duration d = Duration.ZERO;
            if (days != 0) d = d.plusDays(days);
            if (hours != 0) d = d.plusHours(hours);
            if (minutes != 0) d = d.plusMinutes(minutes);
            if (seconds != 0) d = d.plusSeconds(seconds);
            if (millis != 0) d = d.plusMillis(millis);
            return d;
        }
        return null;
    }
}
