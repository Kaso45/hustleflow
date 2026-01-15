import apiClient from './apiClient';

export const createReview = async (employeeId) => {
    return apiClient.post('/performance-reviews', { employeeId });
};

export const getReviews = async ({ employeeId, page = 0, size = 20, sort = 'reviewDate,desc' } = {}) => {
    const params = { page, size, sort };
    if (employeeId) params.employeeId = employeeId;
    return apiClient.get('/performance-reviews', { params });
};

export default { createReview, getReviews };
