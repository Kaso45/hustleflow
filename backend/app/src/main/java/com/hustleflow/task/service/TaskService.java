package com.hustleflow.task.service;

import com.hustleflow.task.dto.TaskCreateRequest;
import com.hustleflow.task.dto.TaskResponse;
import com.hustleflow.task.dto.TaskStatusUpdateRequest;

import java.util.List;

public interface TaskService {

    TaskResponse createTask(TaskCreateRequest request);

    List<TaskResponse> getTasks(Long projectId, Long assigneeId, String status);

    TaskResponse updateTaskStatus(Long taskId, TaskStatusUpdateRequest request);
}
