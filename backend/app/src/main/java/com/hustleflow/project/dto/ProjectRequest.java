package com.hustleflow.project.dto;

import com.hustleflow.project.enums.ProjectStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProjectRequest {
    private String projectName;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectStatus status;
    private Long managerId;
}

