package com.hustleflow.timesheet.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

import org.springframework.stereotype.Service;

import com.hustleflow.employee.domain.Employee;
import com.hustleflow.timesheet.domain.Timesheet;
import com.hustleflow.timesheet.dto.request.CheckInRequest;
import com.hustleflow.timesheet.dto.request.ClockOutRequest;
import com.hustleflow.timesheet.dto.response.CheckInDataResponse;
import com.hustleflow.timesheet.dto.response.CheckInResponse;
import com.hustleflow.timesheet.dto.response.ClockOutDataResponse;
import com.hustleflow.timesheet.dto.response.ClockOutResponse;
import com.hustleflow.timesheet.dto.response.TimesheetReponse;
import com.hustleflow.timesheet.enums.WorkStatus;
import com.hustleflow.timesheet.repository.TimesheetRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class TimesheetService {

    private static final LocalTime DEADLINE_TIME = LocalTime.of(9, 0); // 9:00 AM deadline

    private TimesheetRepository timesheetRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public TimesheetService(TimesheetRepository timesheetRepository) {
        this.timesheetRepository = timesheetRepository;
    }

    public TimesheetReponse getTimesheetHistory(Long employeeId, String month, String year) {
        LocalDate baseDate;
        if (month == null || month.isBlank() || year == null || year.isBlank()) {
            baseDate = LocalDate.now();
        } else {
            try {
                int y = Integer.parseInt(year);
                int m = Integer.parseInt(month);
                baseDate = LocalDate.of(y, m, 1);
            } catch (Exception e) {
                baseDate = LocalDate.now();
            }
        }

        LocalDateTime start = baseDate.atStartOfDay();
        LocalDateTime end = baseDate.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);

        Employee employeeRef = entityManager.getReference(Employee.class, employeeId);

        Timesheet existing = timesheetRepository.findByEmployeeAndDateBetween(employeeRef, start, end)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Timesheet doesn't exist for employee: " + employeeId));

        String checkInStr = existing.getCheckIn() != null
                ? existing.getCheckIn().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                : null;
        String checkOutStr = existing.getCheckOut() != null
                ? existing.getCheckOut().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                : null;

        return TimesheetReponse.builder()
                .employeeId(existing.getEmployee().getId())
                .date(existing.getDate().toLocalDate().toString())
                .checkIn(checkInStr)
                .checkOut(checkOutStr)
                .totalHours(existing.getTotalHours())
                .status(existing.getStatus())
                .build();
    }

    // Check-in logic
    public CheckInResponse createTimesheet(CheckInRequest request) {
        WorkStatus checkInStatus = WorkStatus.ON_TIME;
        if (request.getTimestamp().toLocalTime().isAfter(DEADLINE_TIME)) {
            checkInStatus = WorkStatus.LATE;
        }

        Employee employeeRef = entityManager.getReference(Employee.class, request.getEmployeeId());

        Timesheet timesheet = Timesheet.builder()
                .employee(employeeRef)
                .date(request.getTimestamp())
                .checkIn(request.getTimestamp().toLocalTime())
                .status(checkInStatus)
                .build();

        timesheetRepository.save(timesheet);
        return CheckInResponse.builder()
                .message("Check-in successful for employee: " + timesheet.getEmployee().getId())
                .data(new CheckInDataResponse(
                        timesheet.getId(),
                        timesheet.getDate(),
                        timesheet.getStatus()))
                .build();
    }

    // clock-out logic
    public ClockOutResponse updateTimesheet(ClockOutRequest request) {
        Employee employeeRef = entityManager.getReference(Employee.class, request.getEmployeeId());
        LocalDate day = request.getTimestamp().toLocalDate();
        LocalDateTime start = day.atStartOfDay();
        LocalDateTime end = day.atTime(LocalTime.MAX);

        Timesheet existing = timesheetRepository.findByEmployeeAndDateBetween(employeeRef, start, end)
                .stream()
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException("Timesheet doesn't exist for employee: " + request.getEmployeeId()));

        existing.setCheckOut(request.getTimestamp().toLocalTime());
        // Calculate total hours worked
        double hoursWorked = Duration.between(existing.getCheckIn(), existing.getCheckOut()).toHours();
        LocalDateTime checkOutTime = request.getTimestamp();
        existing.setTotalHours(hoursWorked);

        timesheetRepository.save(existing);
        return ClockOutResponse.builder()
                .message("Clock-out successful for employee: " + existing.getEmployee().getId())
                .data(new ClockOutDataResponse(existing.getId(), checkOutTime, hoursWorked))
                .build();
    }

}
