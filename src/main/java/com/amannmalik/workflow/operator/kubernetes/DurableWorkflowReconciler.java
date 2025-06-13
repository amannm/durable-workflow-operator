package com.amannmalik.workflow.operator.kubernetes;

import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerConfiguration
public class DurableWorkflowReconciler implements Reconciler<DurableWorkflow> {
    private static final Logger log = LoggerFactory.getLogger(DurableWorkflowReconciler.class);

    @Override
    public UpdateControl<DurableWorkflow> reconcile(DurableWorkflow resource, io.javaoperatorsdk.operator.api.reconciler.Context context) throws Exception {
        log.info("Reconciling Workflow {}", resource.getMetadata().getName());
        if (resource.getStatus() == null) {
            var status = new DurableWorkflowStatus();
            resource.setStatus(status);
            return UpdateControl.patchStatus(resource);
        }
        return UpdateControl.noUpdate();
    }
}
