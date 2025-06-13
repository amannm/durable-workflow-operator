package com.example.workflow.operator;

import dev.restate.sdk.WorkflowContext;
import dev.restate.sdk.annotation.Workflow;
import dev.restate.sdk.endpoint.Endpoint;
import dev.restate.sdk.http.vertx.RestateHttpServer;
import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.deserializers.WorkflowParser;
import io.serverlessworkflow.api.states.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowRunner {
    private static final Logger log = LoggerFactory.getLogger(WorkflowRunner.class);

    public void run(String definition) {
        try {
            Workflow workflow = WorkflowParser.parse(definition);
            log.info("Parsed workflow: {} - version {}", workflow.getId(), workflow.getVersion());
            Endpoint endpoint = Endpoint.bind(new ServerlessWorkflowService(workflow)).build();
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
        private final Workflow workflow;

        ServerlessWorkflowService(Workflow workflow) {
            this.workflow = workflow;
        }

        @Workflow
        public String run(WorkflowContext ctx) {
            for (State state : workflow.getStates()) {
                String name = state.getName();
                ctx.run(name, () -> log.info("Executing state {}", name));
            }
            return "completed";
        }
    }
}
