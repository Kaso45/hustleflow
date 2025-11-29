package com.hustleflow.leave.service;

import com.hustleflow.leave.dto.LeaveCreateRequest;
import com.hustleflow.leave.dto.LeaveResponse;
import com.hustleflow.leave.dto.LeaveStatusUpdateRequest;

import java.util.List;

public interface LeaveService {

    LeaveResponse createLeaveRequest(LeaveCreateRequest leaveRequest);

    LeaveResponse updateLeaveStatus(Long leaveId, LeaveStatusUpdateRequest request);

    List<LeaveResponse> getLeaves(Long employeeId, String status);
}