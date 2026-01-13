package com.hustleflow.project.service;

import com.hustleflow.project.domain.Project;
import com.hustleflow.project.dto.ProjectRequest;
import com.hustleflow.project.dto.ProjectResponse;
import com.hustleflow.project.repository.ProjectRepository;
import com.hustleflow.employee.domain.Employee;
import com.hustleflow.employee.repository.EmployeeRepository;
import com.hustleflow.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;

    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProjectResponse createProject(ProjectRequest request) {
        Project project = new Project();

        Employee employee = employeeRepository.findById(request.getManagerId())
                .orElseThrow(() -> new RuntimeException("Employee not found: " + request.getManagerId()));

        project.setProjectName(request.getProjectName());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setStatus(request.getStatus());
        project.setManager(employee);

        Project savedProject = projectRepository.save(project);
        return mapToResponse(savedProject);
    }

    public ProjectResponse updateProject(Long projectId, ProjectRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        Employee employee = employeeRepository.findById(request.getManagerId())
                .orElseThrow(() -> new RuntimeException("Employee not found: " + request.getManagerId()));

        if (request.getProjectName() != null) {
            project.setProjectName(request.getProjectName());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }
        if (request.getStartDate() != null) {
            project.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            project.setEndDate(request.getEndDate());
        }
        if (request.getStatus() != null) {
            project.setStatus(request.getStatus());
        }
        if (request.getManagerId() != null) {
            project.setManager(employee);
        }

        Project updatedProject = projectRepository.save(project);
        return mapToResponse(updatedProject);
    }

    private ProjectResponse mapToResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getProjectName(),
                project.getDescription(),
                project.getStartDate(),
                project.getEndDate(),
                project.getStatus(),
                project.getManager().getId());
    }
}
