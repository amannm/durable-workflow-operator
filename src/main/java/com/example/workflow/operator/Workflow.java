package com.example.workflow.operator;

import io.fabric8.kubernetes.client.CustomResource;

public class Workflow extends CustomResource<WorkflowSpec, WorkflowStatus> {
    // no additional fields
}
