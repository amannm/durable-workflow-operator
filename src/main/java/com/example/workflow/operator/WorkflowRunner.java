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
        try {
            ServerlessWorkflow serverlessWorkflow = ServerlessWorkflowParser.parse(definition);
            log.info("Parsed workflow: {} - version {}", serverlessWorkflow.getId(), serverlessWorkflow.getVersion());
            Endpoint endpoint = Endpoint.bind(new ServerlessWorkflowService(serverlessWorkflow)).build();
            RestateHttpServer.listen(endpoint);
        } catch (Exception e) {
            log.error("Failed to parse workflow", e);
        }
    }

    /**
     * Simple service that executes each state of the given workflow sequentially.
     * This is obviously not a production ready implementation, but it shows how
     * Restate can be used together with the Serverless Workflow parser.
     */
    static class ServerlessWorkflowService {
        private final ServerlessWorkflow serverlessWorkflow;

        ServerlessWorkflowService(ServerlessWorkflow serverlessWorkflow) {
            this.serverlessWorkflow = serverlessWorkflow;
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
                });
            }
            return "completed";
        }
    }
}
