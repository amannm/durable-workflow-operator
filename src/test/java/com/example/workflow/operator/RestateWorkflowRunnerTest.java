package com.example.workflow.operator;

import com.example.workflow.operator.model.ServerlessState;
import com.example.workflow.operator.model.ServerlessWorkflow;
import dev.restate.sdk.WorkflowContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.Map;

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
        wf.getStates().add(state);

        RestateWorkflowRunner runner = new RestateWorkflowRunner();
        RestateWorkflowRunner.ServerlessWorkflowService service = runner.startService(wf);

        WorkflowContext ctx = Mockito.mock(WorkflowContext.class);
        Mockito.doAnswer(invocation -> {
            Runnable r = invocation.getArgument(1);
            r.run();
            return null;
        }).when(ctx).run(Mockito.anyString(), Mockito.any(Runnable.class));

        long start = System.currentTimeMillis();
        String result = service.run(ctx);
        long elapsed = System.currentTimeMillis() - start;

        assertEquals("completed", result);
        assertEquals("true", service.getData().get("done"));
        // verify wait happened (at least 40ms to account for jitter)
        assert(elapsed >= 40);
    }
}
