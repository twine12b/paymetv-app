package com.paymetv.app.temporal.activities;

import com.paymetv.app.service.FileUploadService;
import com.paymetv.app.service.ImageMaskService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class FileUploadActivityImpl implements FileUploadActivity {

    private final FileUploadService fileUploadService;
    private final ImageMaskService imageMaskService;

    public FileUploadActivityImpl(FileUploadService fileUploadService, ImageMaskService imageMaskService) {
        this.fileUploadService = fileUploadService;
        this.imageMaskService = imageMaskService;
    }

    @Override
    public String execute() {
        return fileUploadService.sayHi();
    }

    @Override
    public String uploadFile(MultipartFile multipartFile, String userDir) {
        System.out.println("Uploading file: " + multipartFile.getOriginalFilename() + " to directory: " + userDir);

        try {
            return fileUploadService.saveFile(multipartFile, userDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
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
    public String imageMask(MultipartFile multipartFile, String userDir) {
        return imageMaskService.sayHi();
    }
}
