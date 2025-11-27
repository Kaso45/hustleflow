package com.hustleflow.task.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // tạm coi projectId & assigneeId là Long
    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "assignee_id", nullable = false)
    private Long assigneeId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    private LocalDateTime deadline;

    // String: "LOW", "MEDIUM", "HIGH"
    @Column(nullable = false, length = 20)
    private String priority;

    // String: "TODO", "IN_PROGRESS", "DONE"
    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "completion_note", columnDefinition = "text")
    private String completionNote;
}
