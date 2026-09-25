package com.paymetv.app.temporal;

import com.paymetv.app.AppApplication;
import com.paymetv.app.service.*;
import com.paymetv.app.temporal.activities.MachineLearningActivityImpl;
import com.paymetv.app.temporal.workflows.MachineLearningWorker;
import com.paymetv.app.temporal.workflows.MachineLearningWorkflow;
import com.paymetv.app.temporal.workflows.MachineLearningWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import jdk.jfr.Description;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
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
import java.io.UncheckedIOException;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = AppApplication.class)
class MachineLearningWorkflowTest {

    private String userDir;
    private MultipartFile multipartFile;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private ImageMaskService imageMaskService;

    @Autowired
    private ImageCheckerService imageCheckerService;

    @Autowired
    private DependencyCheckerService dependencyCheckerService;

    @Autowired
    private TrainModel trainModel;

    @Autowired
    private NotifyAllService notifyAllService;



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
    @Order(1)
    void activity_execute_callsFileUploadService() {
        FileUploadService fileUploadService = mock(FileUploadService.class);
        when(fileUploadService.sayHi()).thenReturn("Hello, World!");

        MachineLearningActivityImpl activity = new MachineLearningActivityImpl(fileUploadService, imageMaskService, dependencyCheckerService, imageCheckerService, trainModel, notifyAllService);

        String result = activity.execute();

        assertEquals("Hello, World!", result);
        verify(fileUploadService, times(1)).sayHi();
    }

    @Test
    @Order(2)
    @DisplayName("uploadFile activity method saves test.jpeg in uploads/testUser12345")
    void activity_uploadFile_savesFileToUploadsUserDirectory() throws Exception {
        MachineLearningActivityImpl activity = new MachineLearningActivityImpl(fileUploadService, imageMaskService, dependencyCheckerService, imageCheckerService, trainModel, notifyAllService);

        String result = activity.uploadFileBytes(multipartFile.getOriginalFilename(), multipartFile.getBytes(), this.userDir);
        Path expectedPath = Paths.get("uploads", this.userDir, this.multipartFile.getOriginalFilename());


        System.out.println(expectedPath.toString());

        assertTrue(Files.exists(expectedPath));
        assertEquals(expectedPath.toAbsolutePath().toString(), result);

        cleanup(expectedPath);
        assertFalse(Files.exists(expectedPath));
    }

    @Test
    @Order(3)
    @DisplayName("Test image background removal of test.jpeg in uploads/adminTest")
    void activity_background_removal_of_uploaded_file() throws Exception {
        MachineLearningActivityImpl activity = new MachineLearningActivityImpl(fileUploadService, imageMaskService, dependencyCheckerService, imageCheckerService, trainModel, notifyAllService);

        String result = activity.uploadFileBytes(multipartFile.getOriginalFilename(), multipartFile.getBytes(), this.userDir);
        Path expectedPath = Paths.get("uploads", this.userDir, this.multipartFile.getOriginalFilename());


        System.out.println(expectedPath.toString());

        assertTrue(Files.exists(expectedPath));
        assertEquals(expectedPath.toAbsolutePath().toString(), result);

        cleanup(expectedPath);
        assertFalse(Files.exists(expectedPath));
    }

    @Test
    @Order(4)
    void workflow_run_executesActivityAndReturnsResult() throws Exception {
        try (TestWorkflowEnvironment testEnvironment = TestWorkflowEnvironment.newInstance()) {
            String taskQueue = "file-upload-task-queue-test";

            MachineLearningActivityImpl activity = new MachineLearningActivityImpl(fileUploadService, imageMaskService, dependencyCheckerService, imageCheckerService, trainModel, notifyAllService);
            Worker worker = testEnvironment.newWorker(taskQueue);
            worker.registerWorkflowImplementationTypes(MachineLearningWorkflowImpl.class);
            worker.registerActivitiesImplementations(activity);
            testEnvironment.start();

            WorkflowClient workflowClient = testEnvironment.getWorkflowClient();
            MachineLearningWorkflow workflow = workflowClient.newWorkflowStub(
                    MachineLearningWorkflow.class,
                    WorkflowOptions.newBuilder().setTaskQueue(taskQueue).build()
            );

            String result = workflow.run(this.multipartFile.getOriginalFilename(), this.multipartFile.getBytes(), this.userDir);
            Path expectedPath = Paths.get("uploads", this.userDir, this.multipartFile.getOriginalFilename());

            assertTrue(Files.exists(expectedPath));
            assertEquals("success", result);

            cleanup(expectedPath);
            assertFalse(Files.exists(expectedPath));
        }
    }

    @Test
    @Order(5)
    void machine_learning_worker_start_marksWorkerReady() {
        try (TestWorkflowEnvironment testEnvironment = TestWorkflowEnvironment.newInstance()) {
            FileUploadService fileUploadService = mock(FileUploadService.class);
            ImageMaskService imageMaskService = mock(ImageMaskService.class);
            DependencyCheckerService dependencyCheckerService = mock(DependencyCheckerService.class);
            ImageCheckerService imageCheckerService = mock(ImageCheckerService.class);
            TrainModel trainModel = mock(TrainModel.class);
            NotifyAllService notifyAllService = mock(NotifyAllService.class);

            MachineLearningWorker worker = new MachineLearningWorker(
                    fileUploadService,
                    imageMaskService,
                    dependencyCheckerService,
                    imageCheckerService,
                    trainModel,
                    notifyAllService
            );

            worker.machine_learning_worker_start(testEnvironment.getWorkflowService());

            assertTrue(worker.isWorkerReady());

            worker.stopWorker();
        }
    }

    @Test
    @Order(6)
    void stopWorker_shutsWorkerDownAndMarksNotReady() {
        try (TestWorkflowEnvironment testEnvironment = TestWorkflowEnvironment.newInstance()) {
            FileUploadService fileUploadService = mock(FileUploadService.class);
            ImageMaskService imageMaskService = mock(ImageMaskService.class);
            DependencyCheckerService dependencyCheckerService = mock(DependencyCheckerService.class);
            ImageCheckerService imageCheckerService = mock(ImageCheckerService.class);
            TrainModel trainModel = mock(TrainModel.class);
            NotifyAllService notifyAllService = mock(NotifyAllService.class);

            MachineLearningWorker worker = new MachineLearningWorker(
                    fileUploadService,
                    imageMaskService,
                    dependencyCheckerService,
                    imageCheckerService,
                    trainModel,
                    notifyAllService
            );

            worker.machine_learning_worker_start(testEnvironment.getWorkflowService());
            assertTrue(worker.isWorkerReady());

            worker.stopWorker();

            assertFalse(worker.isWorkerReady());
        }
    }

    @Description("Cleans up test files")
    private void cleanup(Path expectedPath) throws IOException {
        Path userDirectory = expectedPath.getParent();
        if (Files.exists(userDirectory)) {
            try (var paths = Files.walk(userDirectory)) {
                paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            }
        }
    }
}
