package com.hustleflow.employee.service;

import com.hustleflow.employee.domain.Employee;
import com.hustleflow.employee.dto.EmployeeCreateRequest;
import com.hustleflow.employee.dto.EmployeeResponse;
import com.hustleflow.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeResponse createEmployee(EmployeeCreateRequest request) {
        Employee employee = new Employee();
        employee.setEmpDepartment(request.getEmpDepartment());
        employee.setGender(request.getGender());
        employee.setAge(request.getAge());
        employee.setEducationBackground(request.getEducationBackground());
        employee.setPerformanceScore(request.getPerformanceScore());
        employee.setOverTime(request.getOverTime());
        employee.setNumCompaniesWorked(request.getNumCompaniesWorked());
        employee.setEmpJobLevel(request.getEmpJobLevel());
        employee.setEmpJobInvolvement(request.getEmpJobInvolvement());
        employee.setEmpHourlyRate(request.getEmpHourlyRate());
        employee.setEmpJobSatisfaction(request.getEmpJobSatisfaction());
        employee.setEmpEnvironmentSatisfaction(request.getEmpEnvironmentSatisfaction());
        employee.setMaritalStatus(request.getMaritalStatus());
        employee.setEmpJobRole(request.getEmpJobRole());
        employee.setBusinessTravelFrequency(request.getBusinessTravelFrequency());
        employee.setDistanceFromHome(request.getDistanceFromHome());
        employee.setEmpEducationLevel(request.getEmpEducationLevel());
        employee.setEmpLastSalaryHikePercent(request.getEmpLastSalaryHikePercent());
        employee.setEmpRelationshipSatisfaction(request.getEmpRelationshipSatisfaction());
        employee.setTotalWorkExperienceInYears(request.getTotalWorkExperienceInYears());
        employee.setTrainingTimesLastYear(request.getTrainingTimesLastYear());
        employee.setEmpWorkLifeBalance(request.getEmpWorkLifeBalance());
        employee.setExperienceYearsAtThisCompany(request.getExperienceYearsAtThisCompany());
        employee.setExperienceYearsInCurrentRole(request.getExperienceYearsInCurrentRole());
        employee.setYearsSinceLastPromotion(request.getYearsSinceLastPromotion());
        employee.setYearsWithCurrManager(request.getYearsWithCurrManager());
        employee.setAttrition(request.getAttrition());
        employee.setPerformanceRating(request.getPerformanceRating());

        Employee savedEmployee = employeeRepository.save(employee);
        return mapToResponse(savedEmployee);
    }

    public List<EmployeeResponse> getEmployees(String department, String position, Boolean resigned) {
        List<Employee> employees;
        if (department != null && position != null) {
            employees = employeeRepository.findByEmpDepartment(department);
        } else {
            employees = employeeRepository.findAll();
        }
        return employees.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public EmployeeResponse getEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
        return mapToResponse(employee);
    }

    public EmployeeResponse updateEmployee(Long employeeId, EmployeeCreateRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        employee.setEmpDepartment(request.getEmpDepartment());
        employee.setGender(request.getGender());
        employee.setAge(request.getAge());
        employee.setEducationBackground(request.getEducationBackground());
        employee.setPerformanceScore(request.getPerformanceScore());
        employee.setOverTime(request.getOverTime());
        employee.setNumCompaniesWorked(request.getNumCompaniesWorked());
        employee.setEmpJobLevel(request.getEmpJobLevel());
        employee.setEmpJobInvolvement(request.getEmpJobInvolvement());
        employee.setEmpHourlyRate(request.getEmpHourlyRate());
        employee.setEmpJobSatisfaction(request.getEmpJobSatisfaction());
        employee.setEmpEnvironmentSatisfaction(request.getEmpEnvironmentSatisfaction());
        employee.setMaritalStatus(request.getMaritalStatus());
        employee.setEmpJobRole(request.getEmpJobRole());
        employee.setBusinessTravelFrequency(request.getBusinessTravelFrequency());
        employee.setDistanceFromHome(request.getDistanceFromHome());
        employee.setEmpEducationLevel(request.getEmpEducationLevel());
        employee.setEmpLastSalaryHikePercent(request.getEmpLastSalaryHikePercent());
        employee.setEmpRelationshipSatisfaction(request.getEmpRelationshipSatisfaction());
        employee.setTotalWorkExperienceInYears(request.getTotalWorkExperienceInYears());
        employee.setTrainingTimesLastYear(request.getTrainingTimesLastYear());
        employee.setEmpWorkLifeBalance(request.getEmpWorkLifeBalance());
        employee.setExperienceYearsAtThisCompany(request.getExperienceYearsAtThisCompany());
        employee.setExperienceYearsInCurrentRole(request.getExperienceYearsInCurrentRole());
        employee.setYearsSinceLastPromotion(request.getYearsSinceLastPromotion());
        employee.setYearsWithCurrManager(request.getYearsWithCurrManager());
        employee.setAttrition(request.getAttrition());
        employee.setPerformanceRating(request.getPerformanceRating());

        Employee updatedEmployee = employeeRepository.save(employee);
        return mapToResponse(updatedEmployee);
    }

    public void deleteEmployee(Long employeeId) {
        employeeRepository.deleteById(employeeId);
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();
        response.setEmpNumber(employee.getEmpNumber());
        response.setEmpDepartment(employee.getEmpDepartment());
        response.setGender(employee.getGender());
        response.setAge(employee.getAge());
        response.setEducationBackground(employee.getEducationBackground());
        response.setPerformanceScore(employee.getPerformanceScore());
        response.setOverTime(employee.getOverTime());
        response.setNumCompaniesWorked(employee.getNumCompaniesWorked());
        response.setEmpJobLevel(employee.getEmpJobLevel());
        response.setEmpJobInvolvement(employee.getEmpJobInvolvement());
        response.setEmpHourlyRate(employee.getEmpHourlyRate());
        response.setEmpJobSatisfaction(employee.getEmpJobSatisfaction());
        response.setEmpEnvironmentSatisfaction(employee.getEmpEnvironmentSatisfaction());
        response.setMaritalStatus(employee.getMaritalStatus());
        response.setEmpJobRole(employee.getEmpJobRole());
        response.setBusinessTravelFrequency(employee.getBusinessTravelFrequency());
        response.setDistanceFromHome(employee.getDistanceFromHome());
        response.setEmpEducationLevel(employee.getEmpEducationLevel());
        response.setEmpLastSalaryHikePercent(employee.getEmpLastSalaryHikePercent());
        response.setEmpRelationshipSatisfaction(employee.getEmpRelationshipSatisfaction());
        response.setTotalWorkExperienceInYears(employee.getTotalWorkExperienceInYears());
        response.setTrainingTimesLastYear(employee.getTrainingTimesLastYear());
        response.setEmpWorkLifeBalance(employee.getEmpWorkLifeBalance());
        response.setExperienceYearsAtThisCompany(employee.getExperienceYearsAtThisCompany());
        response.setExperienceYearsInCurrentRole(employee.getExperienceYearsInCurrentRole());
        response.setYearsSinceLastPromotion(employee.getYearsSinceLastPromotion());
        response.setYearsWithCurrManager(employee.getYearsWithCurrManager());
        response.setAttrition(employee.getAttrition());
        response.setPerformanceRating(employee.getPerformanceRating());
        return response;
    }
}
