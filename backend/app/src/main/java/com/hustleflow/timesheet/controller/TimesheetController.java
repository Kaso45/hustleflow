package com.hustleflow.timesheet.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hustleflow.timesheet.dto.request.CheckInRequest;
import com.hustleflow.timesheet.dto.request.ClockOutRequest;
import com.hustleflow.timesheet.service.TimesheetService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/timesheets")
public class TimesheetController {

    private TimesheetService timesheetService;

    public TimesheetController(TimesheetService timesheetService) {
        this.timesheetService = timesheetService;
    }

    @PostMapping("/clock-in")
    public ResponseEntity<?> checkIn(@RequestBody CheckInRequest request) {

        try {
            return ResponseEntity.ok(timesheetService.createTimesheet(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PatchMapping("/clock-out")
    public ResponseEntity<?> clockOut(@RequestBody ClockOutRequest request) {

        try {
            return ResponseEntity.ok(timesheetService.updateTimesheet(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("")
    public ResponseEntity<?> getTimesheetHistory(@RequestParam Long employeeId,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String year) {

        try {
            return ResponseEntity.ok(timesheetService.getTimesheetHistory(employeeId, month, year));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

}
