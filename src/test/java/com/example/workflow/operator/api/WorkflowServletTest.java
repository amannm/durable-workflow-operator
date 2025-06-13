package com.example.workflow.operator.api;

import com.example.workflow.operator.Workflow;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class WorkflowServletTest {

    static class SimpleServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream delegate;
        SimpleServletInputStream(ByteArrayInputStream delegate) {
            this.delegate = delegate;
        }
        @Override
        public int read() throws IOException {
            return delegate.read();
        }
        @Override
        public boolean isFinished() {
            return delegate.available() == 0;
        }
        @Override
        public boolean isReady() {
            return true;
        }
        @Override
        public void setReadListener(jakarta.servlet.ReadListener readListener) {
            // not used
        }
    }

    @Test
    void createsWorkflowResource() throws Exception {
        KubernetesClient client = Mockito.mock(KubernetesClient.class);
        var op = Mockito.mock(MixedOperation.class);
        Resource<Workflow> resource = Mockito.mock(Resource.class);
        Mockito.when(client.resources(Workflow.class)).thenReturn(op);
        Mockito.when(op.inNamespace("default")).thenReturn(op);
        ArgumentCaptor<Workflow> captor = ArgumentCaptor.forClass(Workflow.class);
        Mockito.when(op.resource(captor.capture())).thenReturn(resource);

        WorkflowServlet servlet = new WorkflowServlet(client);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        String json = "{\"id\":\"test\",\"version\":\"1.0\",\"states\":[]}";
        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        Mockito.when(req.getInputStream()).thenReturn(new SimpleServletInputStream(in));
        StringWriter responseWriter = new StringWriter();
        Mockito.when(resp.getWriter()).thenReturn(new PrintWriter(responseWriter));

        servlet.doPost(req, resp);

        Mockito.verify(resource).create();
        Workflow created = captor.getValue();
        assertNotNull(created.getSpec().getDefinition());
        assertEquals("test", created.getSpec().getDefinition().getId());
        assertEquals("1.0", created.getSpec().getDefinition().getVersion());
    }

    @Test
    void createsWorkflowResourceFromYaml() throws Exception {
        KubernetesClient client = Mockito.mock(KubernetesClient.class);
        var op = Mockito.mock(MixedOperation.class);
        Resource<Workflow> resource = Mockito.mock(Resource.class);
        Mockito.when(client.resources(Workflow.class)).thenReturn(op);
        Mockito.when(op.inNamespace("default")).thenReturn(op);
        ArgumentCaptor<Workflow> captor = ArgumentCaptor.forClass(Workflow.class);
        Mockito.when(op.resource(captor.capture())).thenReturn(resource);

        WorkflowServlet servlet = new WorkflowServlet(client);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        String yaml = java.nio.file.Files.readString(java.nio.file.Path.of("src/test/resources/workflows/sample.yaml"));
        ByteArrayInputStream in = new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8));
        Mockito.when(req.getInputStream()).thenReturn(new SimpleServletInputStream(in));
        Mockito.when(req.getContentType()).thenReturn("application/yaml");
        StringWriter responseWriter = new StringWriter();
        Mockito.when(resp.getWriter()).thenReturn(new PrintWriter(responseWriter));

        servlet.doPost(req, resp);

        Mockito.verify(resource).create();
        Workflow created = captor.getValue();
        assertNotNull(created.getSpec().getDefinition());
        assertEquals("switch-example", created.getSpec().getDefinition().getId());
        assertEquals("0.1.0", created.getSpec().getDefinition().getVersion());
    }
}
