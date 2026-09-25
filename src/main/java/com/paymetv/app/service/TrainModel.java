package com.paymetv.app.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
@Slf4j
public class TrainModel {

    Logger logger = LoggerFactory.getLogger(ImageCheckerService.class);

    @Value("${file.upload-dir}")
    private Path filePath;

    // TODO - read the raw image file and apply the mask to it, then save the masked image to the same directory

    public String sayHi () {
        logger.info("sayHi");
        return "Hello from ImageCheckerService!";
    }

    public String makeDataset() {
        logger.info("makeDataset started");
        return "Creating dataSet";
    }

    public String deleteDataSet() {
        logger.info("deleteDataset started");
        return "Deleting dataSet";
    }

    public String trainModel () {
        logger.info("trainModel started");
        return "Training Model"; }
}