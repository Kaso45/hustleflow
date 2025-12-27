// src/models/auth.js

/**
 * Mapping cho response login từ API về model nội bộ.
 * API response:
 * {
 *   "token": "eyJhbGciOiJIUzI1NiIsInR5...",
 *   "expiresIn": 3600
 * }
 */
export function mapLoginResponse(apiData) {
  return {
    token: apiData?.token ?? '',
    expiresIn: apiData?.expiresIn ?? 0,
  };
}

/**
 * Mapping cho response register.
 * API response:
 * {
 *   "message": "User registered successfully",
 *   "userId": 101
 * }
 */
export function mapRegisterResponse(apiData) {
  return {
    message: apiData?.message ?? '',
    userId: apiData?.userId ?? null,
  };
}
