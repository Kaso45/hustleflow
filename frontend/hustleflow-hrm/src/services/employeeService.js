import apiClient from './apiClient'

// Skeleton service, sau này bạn gọi ở PeopleEmployeesPage
export async function fetchEmployees() {
  const res = await apiClient.get('/employees')
  return res.data
}
