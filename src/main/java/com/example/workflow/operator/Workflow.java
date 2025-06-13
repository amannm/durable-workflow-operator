package com.example.workflow.operator;

import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;
import io.fabric8.kubernetes.model.annotation.Kind;
import io.fabric8.kubernetes.model.annotation.Plural;
import io.fabric8.kubernetes.model.annotation.Singular;
import io.fabric8.kubernetes.model.annotation.ShortNames;

@Group("example.com")
@Version("v1alpha1")
@Kind("Workflow")
@Plural("workflows")
@Singular("workflow")
@ShortNames("wf")
public class Workflow extends CustomResource<WorkflowResourceSpec, WorkflowResourceStatus> {
    private static final long serialVersionUID = 1L;
    // no additional fields
}
