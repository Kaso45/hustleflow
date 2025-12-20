// src/models/dashboard/dashboardMapper.js
import { toEmployeeModel } from './dashboardModel';

export function mapEmployees(rawList = []) {
  return rawList.map(toEmployeeModel);
}
