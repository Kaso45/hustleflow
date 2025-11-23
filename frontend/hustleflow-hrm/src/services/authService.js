import apiClient from './apiClient'

export async function login(username, password) {
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
