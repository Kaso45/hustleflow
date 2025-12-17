/**
 * Join timesheets with employee names by employeeId.
 * Backend-ready: timesheet does NOT own employeeName.
 */
export function mapTimesheetsWithEmployeeName(timesheets = [], employees = []) {
  const employeeMap = Object.fromEntries(
    employees.map(e => [e.EmpNumber, e.employeeName])
  )

  return timesheets.map(t => ({
    ...t,
    employeeName: employeeMap[t.employeeId] || 'Unknown'
  }))
}
