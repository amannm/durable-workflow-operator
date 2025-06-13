package com.example.workflow.operator;

import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reconciler that deploys {@link Workflow} custom resources.
 */

@ControllerConfiguration
public class WorkflowResourceReconciler implements Reconciler<Workflow> {
    private static final Logger log = LoggerFactory.getLogger(WorkflowResourceReconciler.class);
    private final RestateWorkflowRunner runner = new RestateWorkflowRunner();

    @Override
    public UpdateControl<Workflow> reconcile(Workflow resource, io.javaoperatorsdk.operator.api.reconciler.Context context) throws Exception {
        log.info("Reconciling Workflow {}", resource.getMetadata().getName());
        if (resource.getStatus() == null) {
            runner.run(resource.getSpec().getDefinition());
            WorkflowResourceStatus status = new WorkflowResourceStatus();
            status.setPhase("Deployed");
            resource.setStatus(status);
            return UpdateControl.patchStatus(resource);
        }
        return UpdateControl.noUpdate();
    }
}
