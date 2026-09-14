package com.paymetv.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class FileUploadService {

  private static final Logger log = LoggerFactory.getLogger(FileUploadService.class);

  /**
   * This base directory is set locally but will change to a remote FS or S3
   * bucket in production. The directory is used to store uploaded files.
   */
  @Value("${file.upload-dir}")
  private Path filePath;

  public String saveFile(MultipartFile fileData, String userDir) throws IOException {
    return saveFile(fileData.getBytes(), Objects.requireNonNull(fileData.getOriginalFilename()), userDir);
  }

  public String saveFile(byte[] fileData, String filename, String userDir) throws IOException {
    Path directory = Paths.get(setPath(userDir).toUri());

    // Create the directory if it doesn't exist
    Files.createDirectories(directory);

    // Create the file path
    Path filePath = directory.resolve(filename);

    // Write the file
    Files.write(filePath, fileData);

    return filePath.toAbsolutePath().toString();
  }

  private Path setPath(String userDir) {
    String normalizedUserDir = userDir == null ? "" : userDir.strip().replaceFirst("^/+", "");
    return this.filePath.normalize().toAbsolutePath().resolve(normalizedUserDir).normalize();
  }

  public String sayHi() {
    return "Hello, World!";
  }
}
