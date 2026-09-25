package com.paymetv.app.temporal.workflows;

import com.paymetv.app.service.*;
import com.paymetv.app.temporal.activities.MachineLearningActivityImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MachineLearningWorker {

    private Logger logger = LoggerFactory.getLogger(MachineLearningWorker.class);

    private FileUploadService fileUploadService;
    private ImageMaskService imageMaskService;
    private DependencyCheckerService dependencyCheckerService;
    private ImageCheckerService imageCheckerService;
    private TrainModel trainModel;
    private NotifyAllService notifyAllService;
    private WorkflowServiceStubs service;
    private WorkerFactory factory;

    public MachineLearningWorker() {
    }

    public MachineLearningWorker(FileUploadService fileUploadService,
                                 ImageMaskService imageMaskService,
                                 DependencyCheckerService dependencyCheckerService,
                                 ImageCheckerService imageCheckerService,
                                 TrainModel trainModel,
                                 NotifyAllService notifyAllService) {
        this.fileUploadService = fileUploadService;
        this.imageMaskService = imageMaskService;
        this.dependencyCheckerService = dependencyCheckerService;
        this.imageCheckerService = imageCheckerService;
        this.trainModel = trainModel;
        this.notifyAllService = notifyAllService;
    }

    public void machine_learning_worker_start () {
        machine_learning_worker_start(WorkflowServiceStubs.newLocalServiceStubs());
    }

    public void machine_learning_worker_start(WorkflowServiceStubs workflowServiceStubs) {
        this.service = workflowServiceStubs;
        WorkflowClient client = WorkflowClient.newInstance(service);
        this.factory = WorkerFactory.newInstance(client);

        Worker worker = this.factory.newWorker("ml-task-queue");

        worker.registerWorkflowImplementationTypes(MachineLearningWorkflowImpl.class);
        worker.registerActivitiesImplementations(new MachineLearningActivityImpl(
                fileUploadService, imageMaskService,
                dependencyCheckerService, imageCheckerService,
                 trainModel, notifyAllService
        ));

        logger.info("machine_learning_worker_start started");

        this.factory.start();
    }

    public boolean isWorkerReady() {
        logger.info("machine_learning_worker_start isReady");
        return this.factory != null && this.factory.isStarted();
    }

    public void stopWorker() {
        if (this.factory != null) {
            this.factory.shutdownNow();
            this.factory = null;
        }
        if (this.service != null) {
            this.service.shutdownNow();
            this.service = null;
        }

        logger.info("machine_learning_worker_start stopped");
    }
}
