package com.example.workflow.operator;

import dev.restate.sdk.endpoint.Endpoint;
import dev.restate.sdk.http.vertx.RestateHttpServer;
import com.example.workflow.operator.model.ServerlessWorkflow;
import com.example.workflow.operator.model.ServerlessWorkflowParser;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class WorkflowRunnerTest {
    @Test
    void runInvokesParser() {
        ServerlessWorkflow workflow = new ServerlessWorkflow();
        try (MockedStatic<ServerlessWorkflowParser> parser = Mockito.mockStatic(ServerlessWorkflowParser.class);
             MockedStatic<RestateHttpServer> server = Mockito.mockStatic(RestateHttpServer.class)) {
            parser.when(() -> ServerlessWorkflowParser.parse("def")).thenReturn(workflow);
            server.when(() -> RestateHttpServer.listen(Mockito.any(Endpoint.class))).thenReturn(0);

            WorkflowRunner runner = new WorkflowRunner();
            runner.run("def");

            parser.verify(() -> ServerlessWorkflowParser.parse("def"));
            server.verify(() -> RestateHttpServer.listen(Mockito.any(Endpoint.class)));
        }
    }
}
