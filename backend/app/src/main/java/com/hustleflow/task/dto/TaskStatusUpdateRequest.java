package com.hustleflow.task.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatusUpdateRequest {

    @NotBlank
    private String status;

    private String completionNote;
}
