import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  withCredentials: true // 允许跨域请求携带Cookie
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['accessToken'] = token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response.data
  },
  async error => {
    const originalRequest = error.config
    
    // 如果是401错误且不是刷新令牌的请求，尝试刷新令牌
    if (error.response && error.response.status === 401 && 
        !originalRequest._retry && 
        !originalRequest.url.includes('/auth/refresh')) {
      
      originalRequest._retry = true
      
      try {
        // 尝试刷新令牌
        const response = await api.get('/auth/refresh')
        
        if (response.code === 1 && response.data && response.data.accessToken) {
          // 更新token
          const newToken = response.data.accessToken
          localStorage.setItem('token', newToken)
          
          // 更新用户信息
          const { id, accountName, role } = response.data
          const userInfo = {
            id,
            username: accountName,
            role: role === 1 ? 'admin' : 'user'
          }
          localStorage.setItem('userInfo', JSON.stringify(userInfo))
          
          // 更新原始请求的token
          originalRequest.headers['accessToken'] = newToken
          
          // 重试原始请求
          return api(originalRequest)
        }
      } catch (refreshError) {
        // 刷新令牌失败，清除用户信息并跳转到登录页
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        window.location.href = '/login'
      }
    } else if (error.response && error.response.status === 401) {
      // 其他401错误或刷新令牌失败，清除用户信息并跳转到登录页
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      window.location.href = '/login'
    }
    
    return Promise.reject(error)
  }
)

export default api