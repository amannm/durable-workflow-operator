package com.example.workflow.operator;

import io.fabric8.kubernetes.client.CustomResource;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import io.javaoperatorsdk.operator.processing.event.ResourceID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reconciler that deploys {@link Workflow} custom resources.
 */

@ControllerConfiguration
public class WorkflowReconciler implements Reconciler<Workflow> {
    private static final Logger log = LoggerFactory.getLogger(WorkflowReconciler.class);
    private final WorkflowRunner runner = new WorkflowRunner();

    @Override
    public UpdateControl<Workflow> reconcile(Workflow resource, io.javaoperatorsdk.operator.api.reconciler.Context context) throws Exception {
        log.info("Reconciling Workflow {}", resource.getMetadata().getName());
        if (resource.getStatus() == null) {
            runner.run(resource.getSpec().getDefinition());
            WorkflowStatus status = new WorkflowStatus();
            status.setPhase("Deployed");
            resource.setStatus(status);
            return UpdateControl.updateStatus(resource);
        }
        return UpdateControl.noUpdate();
    }
}
