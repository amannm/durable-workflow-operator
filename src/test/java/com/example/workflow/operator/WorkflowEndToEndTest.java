package com.example.workflow.operator;

import com.example.workflow.operator.api.WorkflowApiServer;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import com.example.workflow.operator.WorkflowResourceStatus;
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

@EnableKubernetesMockClient(crud = true)
public class WorkflowEndToEndTest {

    KubernetesMockServer server;
    KubernetesClient client;

    @BeforeEach
    void setup() {
        // register the Workflow custom resource definition so that CRUD operations
        // performed by the client are handled by the mock server
        var ctx = new CustomResourceDefinitionContext.Builder()
                .withGroup("example.com")
                .withVersion("v1alpha1")
                .withPlural("workflows")
                .withScope("Cluster")
                .withKind("Workflow")
                .withName("workflows.example.com")
                .withStatusSubresource(true)
                .build();
        server.expectCustomResource(ctx);
    }

    @AfterEach
    void cleanup() {
        server.clearExpectations();
    }

    @Test
    void workflowIsDeployed() throws Exception {


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

        server.expect().patch()
                .withPath("/apis/example.com/v1alpha1/workflows/" + name + "/status?fieldManager=workflowresourcereconciler&force=true")
                .andReply(200, recordedRequest -> {
                    Workflow w = client.resources(Workflow.class).inNamespace("default").withName(name).get();
                    WorkflowResourceStatus st = new WorkflowResourceStatus();
                    st.setPhase("Deployed");
                    w.setStatus(st);
                    client.resources(Workflow.class).inNamespace("default").resource(w).updateStatus();
                    return w;
                })
                .once();


        Workflow wf = null;
        for (int i = 0; i < 20; i++) {
            wf = client.resources(Workflow.class).inNamespace("default").withName(name).get();
            if (wf != null) {
                break;
            }
            Thread.sleep(100);
        }
        assertNotNull(wf);

        operator.stop();
        operatorThread.join(1000);
        apiServer.stop();
    }
}
