package com.hustleflow.task.service.impl;

import com.hustleflow.employee.domain.Employee;
import com.hustleflow.employee.repository.EmployeeRepository;
import com.hustleflow.exception.ResourceNotFoundException;
import com.hustleflow.project.domain.Project;
import com.hustleflow.project.repository.ProjectRepository;
import com.hustleflow.task.domain.Task;
import com.hustleflow.task.dto.TaskCreateRequest;
import com.hustleflow.task.dto.TaskResponse;
import com.hustleflow.task.dto.TaskStatusUpdateRequest;
import com.hustleflow.task.enums.TaskPriority;
import com.hustleflow.task.enums.TaskStatus;
import com.hustleflow.task.repository.TaskRepository;
import com.hustleflow.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public TaskResponse createTask(TaskCreateRequest request) {
        TaskPriority priority = TaskPriority.valueOf(request.getPriority().toUpperCase());
        TaskStatus status = TaskStatus.valueOf(request.getStatus().toUpperCase());

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + request.getProjectId()));

        Employee employee = employeeRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + request.getAssigneeId()));

        var taskBuilder = Task.builder()
                .project(project)
                .assignee(employee)
                .title(request.getTitle())
                .description(request.getDescription())
                .deadline(request.getDeadline())
                .priority(priority);

        if (status != null) {
            taskBuilder.status(status);
        }

        Task saved = taskRepository.save(taskBuilder.build());

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasks(Long projectId, Long assigneeId, String status) {
        TaskStatus normalizedStatus = null;
        if (status != null) {
            normalizedStatus = TaskStatus.valueOf(status);
        }

        return taskRepository.searchTasks(projectId, assigneeId, normalizedStatus)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public TaskResponse updateTaskStatus(Long taskId, TaskStatusUpdateRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        TaskStatus newStatus = TaskStatus.valueOf(request.getStatus().toUpperCase());
        if (newStatus != null) {
            task.setStatus(newStatus);
        }

        if (request.getCompletionNote() != null) {
            task.setCompletionNote(request.getCompletionNote());
        }

        Task saved = taskRepository.save(task);
        return mapToResponse(saved);
    }

    private TaskResponse mapToResponse(Task task) {
        TaskResponse res = new TaskResponse();
        res.setId(task.getId());
        res.setProjectId(task.getProject().getId());
        res.setAssigneeId(task.getAssignee().getId());
        res.setTitle(task.getTitle());
        res.setDescription(task.getDescription());
        res.setDeadline(task.getDeadline());
        res.setPriority(String.valueOf(task.getPriority()));
        res.setStatus(String.valueOf(task.getStatus()));
        res.setCompletionNote(task.getCompletionNote());
        return res;
    }
}
