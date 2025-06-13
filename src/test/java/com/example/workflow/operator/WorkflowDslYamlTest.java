package com.example.workflow.operator;

import com.example.workflow.operator.model.ServerlessWorkflow;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dev.restate.common.function.ThrowingRunnable;
import dev.restate.sdk.WorkflowContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.io.InputStream;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class WorkflowDslYamlTest {
    @Test
    void runsYamlWorkflow() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/get", exch -> {
            byte[] data = "get".getBytes(StandardCharsets.UTF_8);
            exch.sendResponseHeaders(200, data.length);
            exch.getResponseBody().write(data);
            exch.close();
        });
        server.createContext("/post", exch -> {
            byte[] body = exch.getRequestBody().readAllBytes();
            exch.sendResponseHeaders(200, body.length);
            exch.getResponseBody().write(body);
            exch.close();
        });
        server.start();
        int port = server.getAddress().getPort();

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        ServerlessWorkflow wf;
        try (InputStream in = WorkflowDslYamlTest.class.getResourceAsStream("/workflows/sample.yaml")) {
            String yaml = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            yaml = yaml.replace("FETCH_URL", "http://localhost:" + port + "/get");
            yaml = yaml.replace("POST_URL", "http://localhost:" + port + "/post");
            wf = mapper.readValue(yaml, ServerlessWorkflow.class);
        }

        RestateWorkflowRunner.ServerlessWorkflowService service = new RestateWorkflowRunner.ServerlessWorkflowService(wf);
        WorkflowContext ctx = Mockito.mock(WorkflowContext.class);
        Mockito.doAnswer(invocation -> {
            ThrowingRunnable r = invocation.getArgument(1);
            r.run();
            return null;
        }).when(ctx).run(Mockito.anyString(), Mockito.any(ThrowingRunnable.class));

        String result = service.run(ctx);
        assertEquals("completed", result);
        assertNull(service.getData().get("foo"));
        assertEquals("get", service.getData().get("fetched"));
        assertEquals("hi", service.getData().get("posted"));

        server.stop(0);
    }
}
