package com.hustleflow.project.controller;

import com.hustleflow.project.dto.ProjectRequest;
import com.hustleflow.project.dto.ProjectResponse;
import com.hustleflow.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    // Lấy danh sách dự án: GET /api/projects
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    // Tạo dự án mới: POST /api/projects
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody ProjectRequest request) {
        ProjectResponse newProject = projectService.createProject(request);
        return new ResponseEntity<>(newProject, HttpStatus.CREATED);
    }

    // Cập nhật dự án: PUT /api/projects/{projectId}
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long projectId,
            @RequestBody ProjectRequest request) {
        ProjectResponse updatedProject = projectService.updateProject(projectId, request);
        return ResponseEntity.ok(updatedProject);
    }
}