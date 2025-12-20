// src/services/authService.mock.js

export default {
  async login(credentials) {
    console.log("🔧 MOCK MODE: login()", credentials);

    return {
      token: "mock_token_123456789",
      expiresIn: 3600,
    };
  },

  async register(payload) {
    console.log("🔧 MOCK MODE: register()", payload);

    return {
      message: "Mock: User registered successfully",
      userId: 999,
    };
  },
};

