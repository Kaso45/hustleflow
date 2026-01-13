package com.hustleflow.leave.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import com.hustleflow.employee.domain.Employee;
import com.hustleflow.leave.enums.LeaveStatus;
import com.hustleflow.leave.enums.LeaveType;

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

    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Employee employee;

    @Column(name = "leave_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(columnDefinition = "text")
    private String reason;

    @Builder.Default
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LeaveStatus status = LeaveStatus.PENDING;

    @Column(name = "manager_comment")
    private String managerComment;
}