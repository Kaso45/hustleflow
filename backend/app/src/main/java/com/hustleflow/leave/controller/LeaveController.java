package com.hustleflow.leave.controller;

import com.hustleflow.leave.dto.LeaveCreateRequest;
import com.hustleflow.leave.dto.LeaveResponse;
import com.hustleflow.leave.dto.LeaveStatusUpdateRequest;
import com.hustleflow.leave.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @PostMapping("")
    public ResponseEntity<LeaveResponse> createLeaveRequest(@Valid @RequestBody LeaveCreateRequest leaveRequest) {
        LeaveResponse response = leaveService.createLeaveRequest(leaveRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{leaveId}/status")
    public ResponseEntity<LeaveResponse> approveOrRejectLeave(@PathVariable Long leaveId,
            @Valid @RequestBody LeaveStatusUpdateRequest request) {
        LeaveResponse leave = leaveService.updateLeaveStatus(leaveId, request);
        return ResponseEntity.ok(leave);
    }

    @GetMapping("")
    public List<LeaveResponse> getLeaves(@RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String status) {
        return leaveService.getLeaves(employeeId, status);
    }
}
