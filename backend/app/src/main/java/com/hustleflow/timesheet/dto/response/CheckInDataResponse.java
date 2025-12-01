package com.hustleflow.timesheet.dto.response;

import java.time.LocalDateTime;

import com.hustleflow.timesheet.enums.WorkStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckInDataResponse {

    private Long id;
    private LocalDateTime checkInTime;
    private WorkStatus status;
}
