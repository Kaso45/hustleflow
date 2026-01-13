package com.hustleflow.leave.service.impl;

import com.hustleflow.employee.domain.Employee;
import com.hustleflow.employee.repository.EmployeeRepository;
import com.hustleflow.exception.ResourceNotFoundException;
import com.hustleflow.leave.domain.LeaveRequest;
import com.hustleflow.leave.dto.LeaveCreateRequest;
import com.hustleflow.leave.dto.LeaveResponse;
import com.hustleflow.leave.dto.LeaveStatusUpdateRequest;
import com.hustleflow.leave.enums.LeaveStatus;
import com.hustleflow.leave.enums.LeaveType;
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

    private final EmployeeRepository employeeRepository;

    @Override
    public LeaveResponse createLeaveRequest(LeaveCreateRequest leaveRequest) {
        LeaveRequest leave = new LeaveRequest();

        Employee employee = employeeRepository.findById(leaveRequest.getEmployeeId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Employee not found: " + leaveRequest.getEmployeeId()));

        LeaveType leaveType = LeaveType.valueOf(leaveRequest.getLeaveType().toUpperCase());

        leave.setEmployee(employee);
        leave.setLeaveType(leaveType);
        leave.setStartDate(leaveRequest.getStartDate());
        leave.setEndDate(leaveRequest.getEndDate());
        leave.setReason(leaveRequest.getReason());
        LeaveRequest savedLeave = leaveRepository.save(leave);
        return mapToResponse(savedLeave);
    }

    @Override
    public LeaveResponse updateLeaveStatus(Long leaveId, LeaveStatusUpdateRequest request) {
        LeaveRequest leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with id: " + leaveId));

        LeaveStatus leaveStatus = LeaveStatus.valueOf(request.getStatus().toUpperCase());

        leave.setStatus(leaveStatus);
        leave.setManagerComment(request.getManagerComment());
        LeaveRequest savedLeave = leaveRepository.save(leave);
        return mapToResponse(savedLeave);
    }

    public List<LeaveResponse> getLeaves(Long employeeId, String status) {
        LeaveStatus leaveStatus = null;
        if (status != null && !status.isBlank()) {
            leaveStatus = LeaveStatus.valueOf(status.toUpperCase());
        }

        List<LeaveRequest> leaves;
        if (employeeId != null && leaveStatus != null) {
            leaves = leaveRepository.findByEmployee_IdAndStatus(employeeId, leaveStatus);
        } else if (employeeId != null) {
            leaves = leaveRepository.findByEmployee_Id(employeeId);
        } else if (leaveStatus != null) {
            leaves = leaveRepository.findByStatus(leaveStatus);
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
        res.setEmployeeId(leave.getEmployee().getId());
        res.setLeaveType(leave.getLeaveType());
        res.setStartDate(leave.getStartDate());
        res.setEndDate(leave.getEndDate());
        res.setReason(leave.getReason());
        res.setStatus(leave.getStatus());
        res.setManagerComment(leave.getManagerComment());
        return res;
    }
}