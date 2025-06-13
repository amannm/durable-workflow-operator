package com.example.workflow.operator;

import dev.restate.sdk.endpoint.Endpoint;
import dev.restate.sdk.http.vertx.RestateHttpServer;
import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.deserializers.WorkflowParser;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class WorkflowRunnerTest {
    @Test
    void runInvokesParser() {
        Workflow workflow = new Workflow();
        try (MockedStatic<WorkflowParser> parser = Mockito.mockStatic(WorkflowParser.class);
             MockedStatic<RestateHttpServer> server = Mockito.mockStatic(RestateHttpServer.class)) {
            parser.when(() -> WorkflowParser.parse("def")).thenReturn(workflow);
            server.when(() -> RestateHttpServer.listen(Mockito.any(Endpoint.class))).thenReturn(null);

            WorkflowRunner runner = new WorkflowRunner();
            runner.run("def");

            parser.verify(() -> WorkflowParser.parse("def"));
            server.verify(() -> RestateHttpServer.listen(Mockito.any(Endpoint.class)));
        }
    }
}
