package com.example.workflow.operator.api;

import io.fabric8.kubernetes.client.KubernetesClient;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;

public class WorkflowApiServer {
    private final Server server;

    public WorkflowApiServer(KubernetesClient client) {
        this.server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(new ServletHolder(new WorkflowServlet(client)), "/workflows");
        server.setHandler(context);
    }

    public void start() throws Exception {
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
    }
}
