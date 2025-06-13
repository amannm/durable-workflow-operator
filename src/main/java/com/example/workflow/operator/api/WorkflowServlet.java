package com.example.workflow.operator.api;

import com.example.workflow.operator.Workflow;
import com.example.workflow.operator.WorkflowResourceSpec;
import com.example.workflow.operator.model.ServerlessWorkflow;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.KubernetesClient;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class WorkflowServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(WorkflowServlet.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private transient KubernetesClient client;

    public WorkflowServlet(KubernetesClient client) {
        this.client = client;
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.client = null;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String json = new String(req.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        ServerlessWorkflow definition;
        try {
            definition = MAPPER.readValue(json, ServerlessWorkflow.class);
        } catch (JsonProcessingException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid workflow JSON");
            return;
        }
        Workflow wf = new Workflow();
        ObjectMeta meta = new ObjectMeta();
        meta.setName("wf-" + UUID.randomUUID());
        wf.setMetadata(meta);
        WorkflowResourceSpec spec = new WorkflowResourceSpec();
        spec.setDefinition(definition);
        wf.setSpec(spec);
        try {
            client.resources(Workflow.class).inNamespace("default").resource(wf).create();
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(wf.getMetadata().getName());
        } catch (Exception e) {
            log.error("Failed to create Workflow CR", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
