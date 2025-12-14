package com.hustleflow.department.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeparmentResponseDTO {
    private Long id;
    private String departmentName;
    private String code;
    private String description;
    private ManagerSummaryDto manager;
}
