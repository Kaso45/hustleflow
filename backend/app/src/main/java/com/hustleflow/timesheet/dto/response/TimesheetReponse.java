package com.hustleflow.timesheet.dto.response;

import com.hustleflow.timesheet.enums.WorkStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimesheetReponse {

    private Long employeeId;
    private String date;
    private String checkIn;
    private String checkOut;
    private Double totalHours;
    private WorkStatus status;

}
