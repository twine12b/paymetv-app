package com.paymetv.app.temporal;

import com.paymetv.app.AppApplication;
import com.paymetv.app.service.FileUploadService;
import com.paymetv.app.service.ImageMaskService;
import com.paymetv.app.temporal.activities.FileUploadActivityImpl;
import com.paymetv.app.temporal.workflows.FileUploadWorkflow;
import com.paymetv.app.temporal.workflows.FileUploadWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = AppApplication.class)
class FileUploadWorkflowTest {

    private String userDir;
    private MultipartFile multipartFile;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private ImageMaskService imageMaskService;

    @BeforeEach
    void setUp() throws IOException {
        this.userDir = "testUser12345";
        File file = new File("test.jpeg");
        this.multipartFile = new MockMultipartFile(
                "file",
                file.getName(),
                "image/jpeg",
                Files.readAllBytes(file.toPath())
        );
    }

    @Test
    void activity_execute_callsFileUploadService() {
        FileUploadService fileUploadService = mock(FileUploadService.class);
        when(fileUploadService.sayHi()).thenReturn("Hello, World!");

        FileUploadActivityImpl activity = new FileUploadActivityImpl(fileUploadService, imageMaskService);

        String result = activity.execute();

        assertEquals("Hello, World!", result);
        verify(fileUploadService, times(1)).sayHi();
    }

    @Test
    @DisplayName("uploadFile activity method saves test.jpeg in uploads/testUser12345")
    void activity_uploadFile_savesFileToUploadsUserDirectory() throws Exception {
        FileUploadActivityImpl activity = new FileUploadActivityImpl(fileUploadService, imageMaskService);

        String result = activity.uploadFile(this.multipartFile, this.userDir);
        Path expectedPath = Paths.get("uploads", this.userDir, this.multipartFile.getOriginalFilename());


        System.out.println(expectedPath.toString());

        assertTrue(Files.exists(expectedPath));
        assertEquals(expectedPath.toAbsolutePath().toString(), result);

        Files.deleteIfExists(expectedPath);
        Files.deleteIfExists(expectedPath.getParent());
        assertFalse(Files.exists(expectedPath));
    }

    @Test
    void workflow_run_executesActivityAndReturnsResult() throws Exception {
        try (TestWorkflowEnvironment testEnvironment = TestWorkflowEnvironment.newInstance()) {
            String taskQueue = "file-upload-task-queue-test";

            FileUploadActivityImpl activity = new FileUploadActivityImpl(fileUploadService, imageMaskService);
            Worker worker = testEnvironment.newWorker(taskQueue);
            worker.registerWorkflowImplementationTypes(FileUploadWorkflowImpl.class);
            worker.registerActivitiesImplementations(activity);
            testEnvironment.start();

            WorkflowClient workflowClient = testEnvironment.getWorkflowClient();
            FileUploadWorkflow workflow = workflowClient.newWorkflowStub(
                    FileUploadWorkflow.class,
                    WorkflowOptions.newBuilder().setTaskQueue(taskQueue).build()
            );

            String result = workflow.run(this.multipartFile.getOriginalFilename(), this.multipartFile.getBytes(), this.userDir);
            Path expectedPath = Paths.get("uploads", this.userDir, this.multipartFile.getOriginalFilename());

            assertTrue(Files.exists(expectedPath));
            assertEquals(expectedPath.toAbsolutePath().toString(), result);

            Files.deleteIfExists(expectedPath);
            Files.deleteIfExists(expectedPath.getParent());
        }
    }
    }
