package com.paymetv.app.temporal.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@ActivityInterface
public interface MachineLearningActivity {

    @ActivityMethod
    String execute();

    @ActivityMethod
    String uploadFileBytes(String filename, byte[] fileData, String userDir);

    @ActivityMethod
    String imageBackgroundRemover(String loc, String filename) throws IOException, InterruptedException;

    @ActivityMethod
    String imageMask(String loc, String filename) throws IOException, InterruptedException;

    @ActivityMethod
    String createDataSet();

    @ActivityMethod
    String deleteDataSet();

    @ActivityMethod
    String createModel();

//    @ActivityMethod
//    String imageChecker(MultipartFile file, String userDir);
//
//    @ActivityMethod
//    String dependencyChecker(MultipartFile file, String userDir);

    @Autowired
    String doSuccess();

}
