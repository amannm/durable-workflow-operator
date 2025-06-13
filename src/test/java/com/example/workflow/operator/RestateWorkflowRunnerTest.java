package com.example.workflow.operator;

import com.example.workflow.operator.model.ServerlessState;
import com.example.workflow.operator.model.ServerlessWorkflow;
import dev.restate.sdk.WorkflowContext;
import dev.restate.common.function.ThrowingRunnable;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.Map;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RestateWorkflowRunnerTest {

    @Test
    void executesWaitTask() {
        ServerlessWorkflow wf = new ServerlessWorkflow();
        wf.setId("test");
        wf.setVersion("1.0");

        ServerlessState state = new ServerlessState("wait");
        state.setWait(Duration.ofMillis(50));
        state.setSet(Map.of("done", "true"));
        wf.addState(state);

        RestateWorkflowRunner.ServerlessWorkflowService service = new RestateWorkflowRunner.ServerlessWorkflowService(wf);

        WorkflowContext ctx = Mockito.mock(WorkflowContext.class);
        Mockito.doAnswer(invocation -> {
            ThrowingRunnable r = invocation.getArgument(1);
            r.run();
            return null;
        }).when(ctx).run(Mockito.anyString(), Mockito.any(ThrowingRunnable.class));

        long start = System.currentTimeMillis();
        String result = service.run(ctx);
        long elapsed = System.currentTimeMillis() - start;

        assertEquals("completed", result);
        assertEquals("true", service.getData().get("done"));
        // verify wait happened (at least 40ms to account for jitter)
        assert(elapsed >= 40);
    }

    @Test
    void executesFetchAndLogTasks() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/", exch -> {
            byte[] data = "hello".getBytes(StandardCharsets.UTF_8);
            exch.sendResponseHeaders(200, data.length);
            exch.getResponseBody().write(data);
            exch.close();
        });
        server.start();

        String url = "http://localhost:" + server.getAddress().getPort() + "/";

        ServerlessWorkflow wf = new ServerlessWorkflow();
        wf.setId("fetch");
        wf.setVersion("1.0");

        ServerlessState state = new ServerlessState("fetch");
        state.setFetchUrl(url);
        state.setFetchVar("result");
        state.setLog("doing fetch");
        wf.addState(state);

        RestateWorkflowRunner.ServerlessWorkflowService service = new RestateWorkflowRunner.ServerlessWorkflowService(wf);

        WorkflowContext ctx = Mockito.mock(WorkflowContext.class);
        Mockito.doAnswer(invocation -> {
            ThrowingRunnable r = invocation.getArgument(1);
            r.run();
            return null;
        }).when(ctx).run(Mockito.anyString(), Mockito.any(ThrowingRunnable.class));

        String result = service.run(ctx);
        assertEquals("completed", result);
        assertEquals("hello", service.getData().get("result"));

        server.stop(0);
    }
}
