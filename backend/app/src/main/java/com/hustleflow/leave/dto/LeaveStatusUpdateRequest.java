package com.hustleflow.leave.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LeaveStatusUpdateRequest {
    @NotNull
    @Pattern(regexp = "PENDING|APPROVED|REJECTED", flags = Pattern.Flag.CASE_INSENSITIVE, message = "status must be PENDING, APPROVED, or REJECTED")
    private String status;

    @Size(max = 255)
    private String managerComment;

}