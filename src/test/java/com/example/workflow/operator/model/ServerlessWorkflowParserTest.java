package com.example.workflow.operator.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class ServerlessWorkflowParserTest {

    @Test
    void parsesWaitDurationObject() {
        String json = "{\"id\":\"test\",\"version\":\"1.0\",\"states\":[{\"name\":\"waiter\",\"wait\":{\"seconds\":5}}]}";
        ServerlessWorkflow wf = ServerlessWorkflowParser.parse(json);
        assertEquals(1, wf.getStates().size());
        ServerlessState state = wf.getStates().get(0);
        assertEquals("waiter", state.getName());
        assertEquals(Duration.ofSeconds(5), state.getWait());
    }

    @Test
    void parsesWaitDurationString() {
        String json = "{\"states\":[{\"name\":\"w\",\"wait\":\"PT2S\"}]}";
        ServerlessWorkflow wf = ServerlessWorkflowParser.parse(json);
        ServerlessState state = wf.getStates().get(0);
        assertEquals(Duration.ofSeconds(2), state.getWait());
    }

    @Test
    void parsesSetDataObject() {
        String json = "{\"states\":[{\"name\":\"s\",\"set\":{\"k\":\"v\"}}]}";
        ServerlessWorkflow wf = ServerlessWorkflowParser.parse(json);
        ServerlessState state = wf.getStates().get(0);
        assertNotNull(state.getSet());
        assertEquals("v", state.getSet().get("k"));
    }
}
