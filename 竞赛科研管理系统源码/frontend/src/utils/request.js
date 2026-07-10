import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// Track if we're already redirecting to login to avoid multiple redirects
let isRedirectingToLogin = false

request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

request.interceptors.response.use(
  response => {
    // Skip JSON processing for blob/binary responses
    if (response.config.responseType === 'blob' || response.data instanceof Blob) {
      return response
    }
    const data = response.data
    if (data.code !== 200) {
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message))
    }
    return data
  },
  error => {
    if (error.response) {
      if (error.response.status === 401) {
        if (!isRedirectingToLogin) {
          isRedirectingToLogin = true
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          router.push('/login')
          ElMessage.error('登录已过期，请重新登录')
          // Reset redirect flag after navigation
          setTimeout(() => { isRedirectingToLogin = false }, 1000)
        }
      } else if (error.response.status === 403) {
        ElMessage.error('您没有权限访问此功能，如需帮助请联系管理员')
      } else {
        const serverMsg = error.response.data?.message
        if (serverMsg) {
          ElMessage.error(serverMsg)
        } else {
          ElMessage.error('服务器异常，请稍后重试')
        }
      }
    } else {
      ElMessage.error('网络连接失败，请检查网络或确认服务器已启动')
    }
    return Promise.reject(error)
  }
)

export default request
