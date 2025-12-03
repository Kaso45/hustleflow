import apiClient from './apiClient'

const DEV_FAKE_USER = {
  username: 'dev',
  password: 'dev123',   // bạn tự đặt
}

export async function login(username, password) {
  // DEV MOCK: nếu đúng user/pass này thì bỏ qua backend, trả token fake
  if (import.meta.env.DEV &&
      username === DEV_FAKE_USER.username &&
      password === DEV_FAKE_USER.password) {
    // giả lập delay 300ms cho giống thật
    await new Promise((resolve) => setTimeout(resolve, 300))
    return {
      token: 'dev-fake-token',
      expiresIn: 3600,
    }
  }

  // Còn lại thì gọi API backend thật
  const res = await apiClient.post('/auth/login', {
    username,
    password,
  })
  return res.data
}

export async function register(payload) {
  const res = await apiClient.post('/auth/register', payload)
  return res.data
}
