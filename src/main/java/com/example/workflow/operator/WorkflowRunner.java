package com.example.workflow.operator;

import dev.restate.sdk.WorkflowContext;
import dev.restate.sdk.endpoint.Endpoint;
import dev.restate.sdk.http.vertx.RestateHttpServer;
import com.example.workflow.operator.model.ServerlessWorkflow;
import com.example.workflow.operator.model.ServerlessWorkflowParser;
import com.example.workflow.operator.model.ServerlessState;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowRunner {
    private static final Logger log = LoggerFactory.getLogger(WorkflowRunner.class);

    public void run(String definition) {
        startService(definition);
    }

    ServerlessWorkflowService startService(String definition) {
        try {
            ServerlessWorkflow serverlessWorkflow = ServerlessWorkflowParser.parse(definition);
            log.info("Parsed workflow: {} - version {}", serverlessWorkflow.getId(), serverlessWorkflow.getVersion());
            ServerlessWorkflowService service = new ServerlessWorkflowService(serverlessWorkflow);
            Endpoint endpoint = Endpoint.bind(service).build();
            RestateHttpServer.listen(endpoint);
            return service;
        } catch (Exception e) {
            log.error("Failed to parse workflow", e);
            return null;
        }
    }

    /**
     * Simple service that executes each state of the given workflow sequentially.
     * This is obviously not a production ready implementation, but it shows how
     * Restate can be used together with the Serverless Workflow parser.
     */
    static class ServerlessWorkflowService {
        private final ServerlessWorkflow serverlessWorkflow;
        private final java.util.Map<String, String> data = new java.util.HashMap<>();

        ServerlessWorkflowService(ServerlessWorkflow serverlessWorkflow) {
            this.serverlessWorkflow = serverlessWorkflow;
        }

        java.util.Map<String, String> getData() {
            return data;
        }

        @dev.restate.sdk.annotation.Workflow
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
