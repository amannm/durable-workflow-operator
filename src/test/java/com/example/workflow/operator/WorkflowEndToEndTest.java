package com.example.workflow.operator;

import com.example.workflow.operator.api.WorkflowApiServer;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import io.javaoperatorsdk.operator.Operator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("Kubernetes server mock does not fully support CRDs")
public class WorkflowEndToEndTest {

    KubernetesMockServer server;
    KubernetesClient client;

    @BeforeEach
    void setup() {
        server = new KubernetesMockServer();
        client = server.createClient();
    }

    @AfterEach
    void cleanup() {
        server.destroy();
    }

    @Test
    void workflowIsDeployed() throws Exception {
        KubernetesClient client = server.createClient();

        Operator operator = new Operator(o -> o.withKubernetesClient(client));
        operator.register(new WorkflowResourceReconciler());
        Thread operatorThread = new Thread(operator::start);
        operatorThread.start();

        WorkflowApiServer apiServer = new WorkflowApiServer(client, 0);
        apiServer.start();
        int port = apiServer.getPort();

        String json = "{\"id\":\"test\",\"version\":\"1.0\",\"states\":[]}";
        HttpClient http = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/workflows"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, resp.statusCode());
        String name = resp.body();

        Workflow wf = null;
        for (int i = 0; i < 20; i++) {
            wf = client.resources(Workflow.class).inNamespace("default").withName(name).get();
            if (wf != null && wf.getStatus() != null) {
                break;
            }
            Thread.sleep(100);
        }
        assertNotNull(wf);
        assertNotNull(wf.getStatus());
        assertEquals("Deployed", wf.getStatus().getPhase());

        operator.stop();
        operatorThread.join(1000);
        apiServer.stop();
    }
}
