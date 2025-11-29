package com.hustleflow.leave.dto;

import lombok.Data;

@Data
public class LeaveStatusUpdateRequest {
    private String status;
    private String managerComment;

}