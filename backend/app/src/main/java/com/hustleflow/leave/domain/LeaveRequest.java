package com.hustleflow.leave.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "leave_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // trong API
    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    // "ANNUAL", "SICK", "UNPAID" (string)
    @Column(name = "leave_type", nullable = false, length = 20)
    private String leaveType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(columnDefinition = "text")
    private String reason;

    // "PENDING", "APPROVED", "REJECTED"
    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "manager_comment", columnDefinition = "text")
    private String managerComment;
}