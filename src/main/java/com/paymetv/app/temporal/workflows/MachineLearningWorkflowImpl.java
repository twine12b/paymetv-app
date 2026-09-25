package com.paymetv.app.temporal.workflows;

import com.paymetv.app.service.ImageCheckerService;
import com.paymetv.app.service.ImageMaskService;
import com.paymetv.app.service.NotifyAllService;
import com.paymetv.app.temporal.activities.MachineLearningActivity;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class MachineLearningWorkflowImpl implements MachineLearningWorkflow {

    Logger logger = LoggerFactory.getLogger(MachineLearningWorkflowImpl.class);

    private ImageCheckerService imageCheckerService;
    private ImageMaskService imageMaskService;
    private NotifyAllService notifyAllService;

    private final MachineLearningActivity fileUploadActivity = Workflow.newActivityStub(
            MachineLearningActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .build()
    );

    @Override
    public String run(String filename, byte[] fileData, String userDir) {
        logger.info("File Upload Started");
        fileUploadActivity.uploadFileBytes(filename, fileData, userDir);

        // Remove background image
        try {
            logger.info("Image background removal Started");
            fileUploadActivity.imageBackgroundRemover(userDir, filename);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        // Add image mask
        try {
            logger.info("Image masking Started");
            fileUploadActivity.imageMask(userDir, filename);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        fileUploadActivity.createDataSet();

        fileUploadActivity.deleteDataSet();




return "success";

    }
//
//    @Override
//    public String imageCheck() {
//        return imageCheckerService.sayHi();
//    }

//    @Override
//    public String imageMask() {
//        return imageMaskService.sayHi();
//    }
//
//    @Override
//    public String notifying() {
//        return notifyAllService.sayHi();
//    }
}
