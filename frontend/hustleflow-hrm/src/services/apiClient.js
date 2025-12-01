import axios from 'axios'
import { getToken, clearToken } from '../store/authStore'

// Axios instance dùng chung cho toàn bộ app
const apiClient = axios.create({
  baseURL: '/api', // sửa nếu backend của bạn dùng URL khác
  timeout: 10000,
})

// Gắn token vào mọi request nếu có
apiClient.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Xử lý lỗi 401: token hết hạn / không hợp lệ
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      clearToken()
      // Quăng user về trang login
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default apiClient
