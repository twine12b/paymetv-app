package com.paymetv.app.temporal.workflows;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface MachineLearningWorkflow {

    @WorkflowMethod
    String run(String filename, byte[] fileData, String userDir);

//    @Workflactivities to the MachineLearningWorkflow

//    @WorkflowMethod
//    String imageMask();
//
//    @WorkflowMethod
//    String notifying();
}
