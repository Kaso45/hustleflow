package com.hustleflow.timesheet.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClockOutDataResponse {
    private Long id;
    private LocalDateTime checkOutTime;
    private Double totalHours;
}
