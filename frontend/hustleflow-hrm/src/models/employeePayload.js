// src/models/employeePayload.js

// Map UI form data to backend API schema for POST/PUT /api/employees
export function toEmployeeApiPayload(data = {}) {
    return {
        empDepartmentId:
            data.empDepartmentId === null || data.empDepartmentId === undefined || data.empDepartmentId === ''
                ? null
                : Number(data.empDepartmentId),
        empDepartment: data.empDepartment ?? '',
        name: data.name ?? '',
        gender: data.gender ?? '',
        age: numberOr(data.age, 0),
        educationBackground: data.educationBackground ?? '',
        overTime: booleanOr(data.overTime, false),
        numCompaniesWorked: numberOr(data.numCompaniesWorked, 0),
        empJobLevel: numberOr(data.empJobLevel, 1), // 1-5
        empJobInvolvement: numberOr(data.empJobInvolvement, 1), // 1-5
        empHourlyRate: numberOr(data.empHourlyRate, 0),
        empJobSatisfaction: numberOr(data.empJobSatisfaction, 1), // 1-5
        empEnvironmentSatisfaction: numberOr(data.empEnvironmentSatisfaction, 1), // 1-5
        maritalStatus: data.maritalStatus ?? '',
        empJobRole: data.empJobRole ?? '',
        businessTravelFrequency: data.businessTravelFrequency ?? '',
        distanceFromHome: numberOr(data.distanceFromHome, 0),
        empEducationLevel: numberOr(data.empEducationLevel, 1), // 1-5
        empLastSalaryHikePercent: numberOr(data.empLastSalaryHikePercent, 0), // 0-100
        empRelationshipSatisfaction: numberOr(data.empRelationshipSatisfaction, 1), // 1-5
        totalWorkExperienceInYears: numberOr(data.totalWorkExperienceInYears, 0),
        trainingTimesLastYear: numberOr(data.trainingTimesLastYear, 0),
        empWorkLifeBalance: numberOr(data.empWorkLifeBalance, 1), // 1-5
        experienceYearsAtThisCompany: numberOr(data.experienceYearsAtThisCompany, 0),
        experienceYearsInCurrentRole: numberOr(data.experienceYearsInCurrentRole, 0),
        yearsSinceLastPromotion: numberOr(data.yearsSinceLastPromotion, 0),
        yearsWithCurrManager: numberOr(data.yearsWithCurrManager, 0),
        attrition: booleanOr(data.attrition, false),
        performanceRating:
            data.performanceRating === '' || data.performanceRating === null || data.performanceRating === undefined
                ? null
                : Number(data.performanceRating), // 0-2 or null
    };
}

function numberOr(val, dflt) {
    const n = Number(val);
    return Number.isFinite(n) ? n : dflt;
}

function booleanOr(val, dflt) {
    if (typeof val === 'boolean') return val;
    if (val === 'true') return true;
    if (val === 'false') return false;
    return dflt;
}
