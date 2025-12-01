package com.hustleflow.timesheet.dto.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClockOutRequest {

    private Long employeeId;
    private LocalDateTime timestamp;

}
