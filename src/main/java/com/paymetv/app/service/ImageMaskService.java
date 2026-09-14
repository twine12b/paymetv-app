package com.paymetv.app.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
@Slf4j
public class ImageMaskService {

    Logger logger = LoggerFactory.getLogger(ImageMaskService.class);

    @Value("${file.upload-dir}")
    private Path filePath;

    // TODO - read the raw image file and apply the mask to it, then save the masked image to the same directory

    public String sayHi () { return "Hello from ImageMaskService!";}
}
