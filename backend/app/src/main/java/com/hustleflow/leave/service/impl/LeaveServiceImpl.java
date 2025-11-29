package com.hustleflow.leave.service.impl;

import com.hustleflow.leave.domain.LeaveRequest;
import com.hustleflow.leave.dto.LeaveCreateRequest;
import com.hustleflow.leave.dto.LeaveResponse;
import com.hustleflow.leave.dto.LeaveStatusUpdateRequest;
import com.hustleflow.leave.repository.LeaveRequestRepository;
import com.hustleflow.leave.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRequestRepository leaveRepository;

    @Override
    public LeaveResponse createLeaveRequest(LeaveCreateRequest leaveRequest) {
        LeaveRequest leave = new LeaveRequest();
        leave.setEmployeeId(leaveRequest.getEmployeeId());
        leave.setLeaveType(leaveRequest.getLeaveType());
        leave.setStartDate(leaveRequest.getStartDate());
        leave.setEndDate(leaveRequest.getEndDate());
        leave.setReason(leaveRequest.getReason());
        leave.setStatus(leaveRequest.getStatus());
        LeaveRequest savedLeave = leaveRepository.save(leave);
        return mapToResponse(savedLeave);
    }

    @Override
    public LeaveResponse updateLeaveStatus(Long leaveId, LeaveStatusUpdateRequest request) {
        LeaveRequest leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found with id: " + leaveId));

        leave.setStatus(request.getStatus());
        leave.setManagerComment(request.getManagerComment());
        LeaveRequest savedLeave = leaveRepository.save(leave);
        return mapToResponse(savedLeave);
    }

    public List<LeaveResponse> getLeaves(Long employeeId, String status) {
        List<LeaveRequest> leaves;
        if (employeeId != null && status != null) {
            leaves = leaveRepository.findByEmployeeIdAndStatus(employeeId, status);
        } else if (employeeId != null) {
            leaves = leaveRepository.findByEmployeeId(employeeId);
        } else if (status != null) {
            leaves = leaveRepository.findByStatus(status);
        } else {
            leaves = leaveRepository.findAll();
        }
        return leaves.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private LeaveResponse mapToResponse(LeaveRequest leave) {
        LeaveResponse res = new LeaveResponse();
        res.setId(leave.getId());
        res.setEmployeeId(leave.getEmployeeId());
        res.setLeaveType(leave.getLeaveType());
        res.setStartDate(leave.getStartDate());
        res.setEndDate(leave.getEndDate());
        res.setReason(leave.getReason());
        res.setStatus(leave.getStatus());
        res.setManagerComment(leave.getManagerComment());
        return res;
    }
}