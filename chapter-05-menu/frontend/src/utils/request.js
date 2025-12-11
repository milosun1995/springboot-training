import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from './auth'
import router from '../router'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '', // 使用空字符串，通过 Vite 代理转发到后端
  timeout: 5000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res
    } else {
      // 业务错误：不在这里显示消息，由调用方处理
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  error => {
    // 网络错误或 HTTP 错误：友好的错误提示
    let errorMessage = '请求失败'
    
    if (error.response) {
      // 服务器返回了错误响应
      const status = error.response.status
      const data = error.response.data
      
      if (data && data.message) {
        errorMessage = data.message
      } else {
        // 根据 HTTP 状态码提供友好提示
        switch (status) {
          case 400:
            errorMessage = '请求参数错误'
            break
          case 401:
            errorMessage = '未授权，请重新登录'
            break
          case 403:
            errorMessage = '拒绝访问'
            break
          case 404:
            errorMessage = '请求的资源不存在'
            break
          case 500:
            errorMessage = '服务器内部错误'
            break
          case 502:
            errorMessage = '网关错误'
            break
          case 503:
            errorMessage = '服务不可用'
            break
          case 504:
            errorMessage = '网关超时'
            break
          default:
            errorMessage = `请求失败: ${status}`
        }
      }
      
      // 401 错误需要跳转到登录页
      if (status === 401) {
        removeToken()
        router.push('/login')
      }
    } else if (error.request) {
      // 请求已发出，但没有收到响应
      if (error.code === 'ECONNABORTED') {
        errorMessage = '请求超时，请稍后重试'
      } else if (error.message === 'Network Error') {
        errorMessage = '网络错误，请检查网络连接'
      } else {
        errorMessage = '网络异常，请稍后重试'
      }
    } else {
      // 其他错误
      errorMessage = error.message || '请求失败'
    }
    
    // 注意：不在这里显示消息，由调用方决定是否显示
    // 这样可以避免重复显示错误提示
    const err = new Error(errorMessage)
    err.originalError = error
    return Promise.reject(err)
  }
)

export default service

