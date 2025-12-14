package com.hustleflow.leave.repository;

import com.hustleflow.leave.domain.LeaveRequest;
import com.hustleflow.leave.enums.LeaveStatus;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    @EntityGraph(attributePaths = "employee")
    List<LeaveRequest> findByEmployee_Id(Long employeeId);

    List<LeaveRequest> findByStatus(LeaveStatus status);

    @EntityGraph(attributePaths = "employee")
    List<LeaveRequest> findByEmployee_IdAndStatus(Long employeeId, LeaveStatus status);
}