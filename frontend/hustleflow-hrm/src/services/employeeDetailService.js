import apiClient from './apiClient';
import { USE_MOCK_API } from '@/config/appConfig';

export const getEmployeeById = async (id) => {
    if (USE_MOCK_API) return { data: null };
    return apiClient.get(`/employees/${id}`);
};

export default { getEmployeeById };
