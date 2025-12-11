package com.hustleflow.leave.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class LeaveCreateRequest {

    @NotNull
    private Long employeeId;

    @NotNull
    @Pattern(regexp = "ANNUAL|SICK|UNPAID", flags = Pattern.Flag.CASE_INSENSITIVE, message = "status must be ANNUAL, SICK, or UNPAID")
    private String leaveType;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @Size(max = 2000)
    private String reason;

}