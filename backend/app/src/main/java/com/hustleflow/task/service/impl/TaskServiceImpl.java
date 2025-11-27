package com.hustleflow.task.service.impl;

import com.hustleflow.task.domain.Task;
import com.hustleflow.task.dto.TaskCreateRequest;
import com.hustleflow.task.dto.TaskResponse;
import com.hustleflow.task.dto.TaskStatusUpdateRequest;
import com.hustleflow.task.repository.TaskRepository;
import com.hustleflow.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    public TaskResponse createTask(TaskCreateRequest request) {
        // default priority/status nếu client không gửi
        String priority = request.getPriority();
        if (priority == null || priority.isBlank()) {
            priority = "MEDIUM";
        }
        priority = priority.toUpperCase();

        String status = request.getStatus();
        if (status == null || status.isBlank()) {
            status = "TODO";
        }
        status = status.toUpperCase();

        Task task = Task.builder()
                .projectId(request.getProjectId())
                .assigneeId(request.getAssigneeId())
                .title(request.getTitle())
                .description(request.getDescription())
                .deadline(request.getDeadline())
                .priority(priority)
                .status(status)
                .build();

        Task saved = taskRepository.save(task);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasks(Long projectId, Long assigneeId, String status) {
        String normalizedStatus = null;
        if (status != null && !status.isBlank()) {
            normalizedStatus = status.toUpperCase();
        }

        return taskRepository.searchTasks(projectId, assigneeId, normalizedStatus)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public TaskResponse updateTaskStatus(Long taskId, TaskStatusUpdateRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        String newStatus = request.getStatus();
        task.setStatus(newStatus.toUpperCase());

        if (request.getCompletionNote() != null) {
            task.setCompletionNote(request.getCompletionNote());
        }

        Task saved = taskRepository.save(task);
        return mapToResponse(saved);
    }

    private TaskResponse mapToResponse(Task task) {
        TaskResponse res = new TaskResponse();
        res.setId(task.getId());
        res.setProjectId(task.getProjectId());
        res.setAssigneeId(task.getAssigneeId());
        res.setTitle(task.getTitle());
        res.setDescription(task.getDescription());
        res.setDeadline(task.getDeadline());
        res.setPriority(task.getPriority());
        res.setStatus(task.getStatus());
        res.setCompletionNote(task.getCompletionNote());
        return res;
    }
}
