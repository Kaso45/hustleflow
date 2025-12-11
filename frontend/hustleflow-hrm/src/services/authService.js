import apiClient from './apiClient';
import { mapLoginResponse, mapRegisterResponse } from '../models/auth';
import { USE_MOCK_API } from '../config/appConfig';

import mockService from './authService.mock';

const AuthService = {

  async login(credentials) {
    if (USE_MOCK_API) {
      return mockService.login(credentials);
    }

    const response = await apiClient.post('/auth/login', credentials);
    return mapLoginResponse(response.data);
  },

  async register(payload) {
    if (USE_MOCK_API) {
      return mockService.register(payload);
    }

    const response = await apiClient.post('/auth/register', payload);
    return mapRegisterResponse(response.data);
  },
};

export default AuthService;
