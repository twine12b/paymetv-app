package com.paymetv.app.temporal.activities;

import com.paymetv.app.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class MachineLearningActivityImpl implements MachineLearningActivity {

    Logger logger = LoggerFactory.getLogger(MachineLearningActivityImpl.class);

    private final FileUploadService fileUploadService;
    private final ImageMaskService imageMaskService;
    private final DependencyCheckerService dependencyCheckerService;
    private final ImageCheckerService imageCheckerService;
    private final TrainModel trainModel;
    private final NotifyAllService notifyAllService;

    public MachineLearningActivityImpl(FileUploadService fileUploadService, ImageMaskService imageMaskService
                                       ,DependencyCheckerService dependencyCheckerService
                                       ,ImageCheckerService imageCheckerService
                                       ,TrainModel trainModel
                                       ,NotifyAllService notifyAllService
    ) {
        this.fileUploadService = fileUploadService;
        this.imageMaskService = imageMaskService;
        this.dependencyCheckerService = dependencyCheckerService;
        this.imageCheckerService = imageCheckerService;
        this.trainModel = trainModel;
        this.notifyAllService = notifyAllService;
    }

    @Override
    public String execute() {
        return fileUploadService.sayHi();
    }

    @Override
    public String uploadFileBytes(String filename, byte[] fileData, String userDir) {
        try {
            return fileUploadService.saveFile(fileData, filename, userDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    @Override
    public String imageBackgroundRemover(String loc, String filename) throws IOException, InterruptedException {
        return imageMaskService.removeBackground(loc, filename);
    }

    @Override
    public String imageMask(String loc, String userDir) throws IOException, InterruptedException {
        return imageMaskService.imageMask(loc, userDir);
    }

    @Override
    public String createDataSet() {
        return trainModel.makeDataset();
    }

    @Override
    public String deleteDataSet() {
        try {
            return trainModel.deleteDataSet();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // TODO - fully implement code below

//    @Override
//    public String dependencyChecker(MultipartFile file, String userDir) {
//        return dependencyCheckerService.sayHi();
//    }
//
//    @Override
//    public String imageChecker(MultipartFile file, String userDir) {
//        return imageCheckerService.sayHi();
//    }

    @Override
    public String createModel() {
        return trainModel.sayHi();
    }

    @Override
    public String doSuccess(){
        return notifyAllService.sayHi();
    }

}
