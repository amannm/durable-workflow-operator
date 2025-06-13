package com.example.workflow.operator;

import dev.restate.sdk.annotation.Workflow;
import dev.restate.sdk.WorkflowContext;
import com.example.workflow.operator.model.ServerlessWorkflow;
import com.example.workflow.operator.model.ServerlessState;
import dev.restate.sdk.endpoint.Endpoint;
import dev.restate.sdk.http.vertx.RestateHttpServer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
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
            ServerlessWorkflowService service = new ServerlessWorkflowService(definition);
            Endpoint endpoint =
                    Endpoint.builder()
                            .bind(service)
                            .build();
            RestateHttpServer.listen(endpoint);
            return service;
        } catch (Exception e) {
            log.error("Failed to parse workflow", e);
            return null;
        }
    }

    static class ServerlessWorkflowService {
        private final ServerlessWorkflow serverlessWorkflow;
        private final Map<String, String> data = new HashMap<>();

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
                    if (state.getLog() != null) {
                        log.info("{}", state.getLog());
                    }
                    if (state.getFetchUrl() != null && state.getFetchVar() != null) {
                        HttpClient http = HttpClient.newHttpClient();
                        try {
                            HttpRequest request = HttpRequest.newBuilder(URI.create(state.getFetchUrl())).GET().build();
                            HttpResponse<String> resp = http.send(request, HttpResponse.BodyHandlers.ofString());
                            data.put(state.getFetchVar(), resp.body());
                        } catch (Exception e) {
                            log.error("HTTP fetch failed", e);
                        }
                    }
                    if (state.getPostUrl() != null && state.getPostVar() != null) {
                        HttpClient http = HttpClient.newHttpClient();
                        try {
                            HttpRequest request = HttpRequest.newBuilder(URI.create(state.getPostUrl()))
                                    .POST(HttpRequest.BodyPublishers.ofString(state.getPostBody() == null ? "" : state.getPostBody()))
                                    .build();
                            HttpResponse<String> resp = http.send(request, HttpResponse.BodyHandlers.ofString());
                            data.put(state.getPostVar(), resp.body());
                        } catch (Exception e) {
                            log.error("HTTP post failed", e);
                        }
                    }
                    if (state.getSet() != null) {
                        data.putAll(state.getSet());
                    }
                    if (state.getUnset() != null) {
                        for (String key : state.getUnset()) {
                            data.remove(key);
                        }
                    }
                });
            }
            return "completed";
        }
    }
}
