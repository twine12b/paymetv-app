package com.paymetv.app.temporal.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import org.springframework.web.multipart.MultipartFile;

@ActivityInterface
public interface FileUploadActivity {

    @ActivityMethod
    String execute();

    @ActivityMethod
    String uploadFile(MultipartFile file, String userDir);

    @ActivityMethod
    String uploadFileBytes(String filename, byte[] fileData, String userDir);

    @ActivityMethod
    String imageMask(MultipartFile file, String userDir);
}
