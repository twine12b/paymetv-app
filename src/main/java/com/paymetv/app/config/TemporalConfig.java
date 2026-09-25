package com.paymetv.app.config;

import com.paymetv.app.temporal.activities.MachineLearningActivityImpl;
import com.paymetv.app.temporal.workflows.MachineLearningWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalConfig {

    @Value("${temporal.target:127.0.0.1:7233}")
    private String temporalTarget;

    @Value("${temporal.task-queue:file-upload-task-queue}")
    private String taskQueue;

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
    @ConditionalOnProperty(name = "temporal.worker.enabled", havingValue = "true", matchIfMissing = true)
    public WorkerFactory workerFactory(WorkflowClient workflowClient, MachineLearningActivityImpl fileUploadActivityImpl) {
        WorkerFactory workerFactory = WorkerFactory.newInstance(workflowClient);
        Worker worker = workerFactory.newWorker(taskQueue);
        worker.registerWorkflowImplementationTypes(MachineLearningWorkflowImpl.class);
        worker.registerActivitiesImplementations(fileUploadActivityImpl);
        return workerFactory;
    }
}
