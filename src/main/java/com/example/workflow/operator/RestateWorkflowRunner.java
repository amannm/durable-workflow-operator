package com.example.workflow.operator;

import dev.restate.sdk.annotation.Workflow;
import dev.restate.sdk.WorkflowContext;
import com.example.workflow.operator.model.ServerlessWorkflow;
import com.example.workflow.operator.model.ServerlessState;
import java.time.Duration;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestateWorkflowRunner {
    private static final Logger log = LoggerFactory.getLogger(RestateWorkflowRunner.class);

    public void run(ServerlessWorkflow definition) {
        startService(definition);
    }

    ServerlessWorkflowService startService(ServerlessWorkflow definition) {
        try {
            throw new UnsupportedOperationException("TODO");
        } catch (Exception e) {
            log.error("Failed to parse workflow", e);
            return null;
        }
    }

    static class ServerlessWorkflowService {
        private final ServerlessWorkflow serverlessWorkflow;
        private final java.util.Map<String, String> data = new java.util.HashMap<>();

        ServerlessWorkflowService(ServerlessWorkflow serverlessWorkflow) {
            this.serverlessWorkflow = serverlessWorkflow;
        }

        Map<String, String> getData() {
            return data;
        }

        @Workflow
        public String run(WorkflowContext ctx) {
            for (ServerlessState state : serverlessWorkflow.getStates()) {
                String name = state.getName();
                ctx.run(name, () -> {
                    log.info("Executing state {}", name);
                    Duration wait = state.getWait();
                    if (wait != null && !wait.isZero()) {
                        try {
                            Thread.sleep(wait.toMillis());
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    if (state.getSet() != null) {
                        data.putAll(state.getSet());
                    }
                });
            }
            return "completed";
        }
    }
}
