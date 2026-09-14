package com.paymetv.app.temporal.workflows;

import com.paymetv.app.temporal.activities.FileUploadActivity;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class FileUploadWorkflowImpl implements FileUploadWorkflow {

    private final FileUploadActivity fileUploadActivity = Workflow.newActivityStub(
            FileUploadActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .build()
    );

    @Override
    public String run(String filename, byte[] fileData, String userDir) {
        return fileUploadActivity.uploadFileBytes(filename, fileData, userDir);
    }
}
