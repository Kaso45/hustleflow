package com.hustleflow.task.repository;

import com.hustleflow.task.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("""
            SELECT t FROM Task t
            WHERE (:projectId IS NULL OR t.projectId = :projectId)
              AND (:assigneeId IS NULL OR t.assigneeId = :assigneeId)
              AND (:status IS NULL OR t.status = :status)
            """)
    List<Task> searchTasks(@Param("projectId") Long projectId,
            @Param("assigneeId") Long assigneeId,
            @Param("status") String status);
}
