package com.hustleflow.task.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.hustleflow.employee.domain.Employee;
import com.hustleflow.project.domain.Project;
import com.hustleflow.task.enums.TaskPriority;
import com.hustleflow.task.enums.TaskStatus;

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

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "assignee_id", referencedColumnName = "id")
    private Employee assignee;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    private LocalDateTime deadline;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @Builder.Default
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.TODO;

    @Column(name = "completion_note", columnDefinition = "text")
    private String completionNote;
}
