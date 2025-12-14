package com.hustleflow.leave.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

import com.hustleflow.leave.enums.LeaveStatus;
import com.hustleflow.leave.enums.LeaveType;

@Getter
@Setter
public class LeaveResponse {

    private Long id;
    private Long employeeId;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private LeaveStatus status;
    private String managerComment;
}