package com.paymetv.app.config;

import com.paymetv.app.temporal.activities.FileUploadActivityImpl;
import com.paymetv.app.temporal.workflows.FileUploadWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalConfig {

    @Value("${temporal.target:127.0.0.1:7233}")
    private String temporalTarget;

    @Value("${temporal.task-queue:file-upload-task-queue}")
    private String taskQueue;

    @Value("${temporal.worker.enabled:true}")
    private boolean temporalWorkerEnabled;

    @Bean(destroyMethod = "shutdown")
    public WorkflowServiceStubs workflowServiceStubs() {
        return WorkflowServiceStubs.newServiceStubs(
                WorkflowServiceStubsOptions.newBuilder()
                        .setTarget(temporalTarget)
                        .build()
        );
    }

    @Bean
    public WorkflowClient workflowClient(WorkflowServiceStubs workflowServiceStubs) {
        return WorkflowClient.newInstance(workflowServiceStubs);
    }

    @Bean(destroyMethod = "shutdown")
    public WorkerFactory workerFactory(WorkflowClient workflowClient, FileUploadActivityImpl fileUploadActivityImpl) {
        WorkerFactory workerFactory = WorkerFactory.newInstance(workflowClient);
        Worker worker = workerFactory.newWorker(taskQueue);
        worker.registerWorkflowImplementationTypes(FileUploadWorkflowImpl.class);
        worker.registerActivitiesImplementations(fileUploadActivityImpl);
        if (temporalWorkerEnabled) {
            workerFactory.start();
        }
        return workerFactory;
    }
}
