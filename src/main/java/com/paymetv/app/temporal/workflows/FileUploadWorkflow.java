package com.paymetv.app.temporal.workflows;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface FileUploadWorkflow {

    @WorkflowMethod
    String run(String filename, byte[] fileData, String userDir);
}
