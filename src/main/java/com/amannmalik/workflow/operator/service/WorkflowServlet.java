package com.amannmalik.workflow.operator.service;

import com.amannmalik.workflow.operator.kubernetes.DurableWorkflow;
import com.amannmalik.workflow.operator.kubernetes.DurableWorkflowSpec;
import io.serverlessworkflow.api.WorkflowFormat;
import io.serverlessworkflow.api.WorkflowReader;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.KubernetesClient;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serial;
import java.util.UUID;

public class WorkflowServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(WorkflowServlet.class);
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
        try {
            if (req.getContentType() != null && req.getContentType().toLowerCase().contains("yaml")) {
                var definition = WorkflowReader.readWorkflow(req.getInputStream(), WorkflowFormat.YAML);
                var wf = new DurableWorkflow();
                var meta = new ObjectMeta();
                meta.setName("wf-" + UUID.randomUUID());
                wf.setMetadata(meta);
                var spec = new DurableWorkflowSpec();
                spec.setDefinition(definition);
                wf.setSpec(spec);
                try {
                    client.resources(DurableWorkflow.class).inNamespace("default").resource(wf).create();
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    resp.getWriter().write(wf.getMetadata().getName());
                } catch (Exception e) {
                    log.error("Failed to create Workflow CR", e);
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                }
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid workflow definition");
            return;
        }

    }
}
