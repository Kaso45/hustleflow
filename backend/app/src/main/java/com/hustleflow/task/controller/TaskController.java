package com.hustleflow.task.controller;

import com.hustleflow.task.dto.TaskCreateRequest;
import com.hustleflow.task.dto.TaskResponse;
import com.hustleflow.task.dto.TaskStatusUpdateRequest;
import com.hustleflow.task.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskCreateRequest request) {
        TaskResponse response = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<TaskResponse> getTasks(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) String status) {
        return taskService.getTasks(projectId, assigneeId, status);
    }

    @PatchMapping("/{taskId}")
    public TaskResponse updateTaskStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskStatusUpdateRequest request) {
        return taskService.updateTaskStatus(taskId, request);
    }
}
