package com.hustleflow.department.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentRequestDTO {

    @NotNull
    private String departmentName;

    @NotNull
    private String code;

    private String description;

    @NotNull
    private Long managerId;
}
