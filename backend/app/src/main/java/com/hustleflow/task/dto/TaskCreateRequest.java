package com.hustleflow.task.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaskCreateRequest {

    @NotNull
    private Long projectId;

    @NotNull
    private Long assigneeId;

    @NotBlank
    private String title;

    private String description;

    private LocalDateTime deadline;

    // cho phép null, service sẽ set default "MEDIUM"
    private String priority;

    // cho phép null, service sẽ set default "TODO"
    private String status;
}
