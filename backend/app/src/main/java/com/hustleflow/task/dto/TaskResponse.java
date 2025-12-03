package com.hustleflow.task.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaskResponse {

    private Long id;
    private Long projectId;
    private Long assigneeId;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private String priority;
    private String status;
    private String completionNote;
}
