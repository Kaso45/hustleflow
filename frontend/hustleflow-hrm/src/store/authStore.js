// src/store/authStore.js
import { defineStore } from 'pinia';
import AuthService from '../services/authService';

const TOKEN_KEY = 'hf_auth_token';
const EXPIRES_AT_KEY = 'hf_auth_expires_at';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: typeof window !== 'undefined' ? localStorage.getItem(TOKEN_KEY) || null : null,
    expiresAt: typeof window !== 'undefined'
      ? Number(localStorage.getItem(EXPIRES_AT_KEY)) || null
      : null,
    loading: false,
    error: null,        // string | null
  }),

  getters: {
    isAuthenticated(state) {
      if (!state.token) return false;
      if (!state.expiresAt) return true;
      return state.expiresAt > Date.now();
    },
  },

  actions: {
    _setToken(token, expiresInSeconds) {
      this.token = token;

      const now = Date.now();
      const expiresAt = expiresInSeconds
        ? now + expiresInSeconds * 1000
        : null;

      this.expiresAt = expiresAt;

      if (typeof window !== 'undefined') {
        if (token) {
          localStorage.setItem(TOKEN_KEY, token);
        } else {
          localStorage.removeItem(TOKEN_KEY);
        }

        if (expiresAt) {
          localStorage.setItem(EXPIRES_AT_KEY, String(expiresAt));
        } else {
          localStorage.removeItem(EXPIRES_AT_KEY);
        }
      }
    },

    _setError(message) {
      this.error = message || 'Something went wrong. Please try again.';
    },

    clearError() {
      this.error = null;
    },

    logout() {
      this._setToken(null, 0);
      this.loading = false;
      this.error = null;
    },

    /**
     * Đăng nhập
     * @param {{ username: string, password: string }} credentials
     */
    async login(credentials) {
      this.loading = true;
      this.clearError();
      try {
        const { token, expiresIn } = await AuthService.login(credentials);
        this._setToken(token, expiresIn);
        return true;
      } catch (error) {
        // Lấy message từ server nếu có
        const message =
          error?.response?.data?.message ||
          error?.message ||
          'Login failed. Please check your credentials.';
        this._setError(message);
        this._setToken(null, 0);
        return false;
      } finally {
        this.loading = false;
      }
    },

    /**
     * Đăng ký
     * @param {{ username: string, password: string, email: string }} payload
     */
    async register(payload) {
      this.loading = true;
      this.clearError();
      try {
        const { message, userId } = await AuthService.register(payload);
        // Có thể tự quyết: sau khi đăng ký có auto login hay không.
        // Ở đây: chỉ trả về thông tin, không auto login.
        return { success: true, message, userId };
      } catch (error) {
        const message =
          error?.response?.data?.message ||
          error?.message ||
          'Registration failed. Please try again.';
        this._setError(message);
        return { success: false, message };
      } finally {
        this.loading = false;
      }
    },
  },
});
