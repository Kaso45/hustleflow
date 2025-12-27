// src/models/employeeMapper.js

/**
 * Map raw employee object (from GET /api/employees) to UI-friendly object.
 * Handles the API docs variant which returns fields like EmpNumber, EmpDepartment, Attrition...
 */
export const mapEmployeeList = (rawEmployees = []) => {
  return rawEmployees.map(emp => mapEmployee(emp));
};

export const mapEmployee = (emp = {}) => {
  // Some fields in docs have slightly different names/casing; guard with fallback.
  return {
    id: emp.EmpNumber ?? emp.Employee_ID ?? emp.EmployeeId ?? null,
    department: emp.EmpDepartment ?? emp.Department ?? null,
    gender: emp.Gender ?? null,
    age: emp.Age ?? null,
    educationBackground: emp.EducationBackground ?? emp.EmpEducationLevel ?? emp.Education_Level ?? null,
    performanceScore: emp.Performance_Score ?? emp.PerformanceRating ?? null,
    overtime: typeof emp.OverTime !== 'undefined' ? emp.OverTime : (emp.Overtime_Hours ? true : false),
    numCompaniesWorked: emp.NumCompaniesWorked ?? null,
    jobLevel: emp.EmpJobLevel ?? null,
    jobInvolvement: emp.EmpJobInvolvement ?? null,
    hourlyRate: emp.EmpHourlyRate ?? null,
    jobSatisfaction: emp.EmpJobSatisfaction ?? null,
    environmentSatisfaction: emp.EmpEnvironmentSatisfaction ?? null,
    maritalStatus: emp.MaritalStatus ?? null,
    jobRole: emp.EmpJobRole ?? emp.Job_Title ?? null,
    businessTravelFrequency: emp.BusinessTravelFrequency ?? null,
    distanceFromHome: emp.DistanceFromHome ?? null,
    lastSalaryHikePercent: emp.EmpLastSalaryHikePercent ?? null,
    relationshipSatisfaction: emp.EmpRelationshipSatisfaction ?? null,
    totalWorkExperienceYears: emp.TotalWorkExperienceInYears ?? null,
    trainingTimesLastYear: emp.TrainingTimesLastYear ?? null,
    workLifeBalance: emp.EmpWorkLifeBalance ?? null,
    yearsAtCompany: emp.ExperienceYearsAtThisCompany ?? emp.Years_At_Company ?? null,
    yearsInCurrentRole: emp.ExperienceYearsInCurrentRole ?? null,
    yearsSinceLastPromotion: emp.YearsSinceLastPromotion ?? null,
    yearsWithCurrManager: emp.YearsWithCurrManager ?? null,
    attrition: typeof emp.Attrition !== 'undefined' ? emp.Attrition : emp.Resigned ?? false,
    performanceRating: emp.PerformanceRating ?? null,

    // Keep raw object if you ever need more fields
    raw: emp,
  };
};
