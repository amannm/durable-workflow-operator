package com.example.workflow.operator;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.javaoperatorsdk.operator.Operator;
import com.example.workflow.operator.api.WorkflowApiServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Starting Durable Workflow Operator");
        try (DefaultKubernetesClient client = new DefaultKubernetesClient(Config.autoConfigure(null))) {
            Operator operator = new Operator();
            operator.register(new WorkflowResourceReconciler());
            WorkflowApiServer apiServer = new WorkflowApiServer(client);
            apiServer.start();
            operator.start();
            apiServer.stop();
        } catch (Exception e) {
            log.error("Operator failed", e);
        }
    }
}
