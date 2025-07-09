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

// 用户相关接口
export const userApi = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/user/register', data),
  getUserInfo: () => api.get('/user/info'),
  updateUserInfo: (data) => api.put('/user/info', data),
  logout: () => api.get('/auth/logout'),
  refreshToken: () => api.get('/auth/refresh')
}

// 设备相关接口
export const equipmentApi = {
  getEquipmentList: (params) => api.get('/equipment', { params }),
  getEquipmentDetail: (id) => api.get(`/equipment/${id}`),
  borrowEquipment: (id, data) => api.post(`/equipment/${id}/borrow`, data),
  returnEquipment: (id) => api.post(`/equipment/${id}/return`)
}

// 项目相关接口
export const projectApi = {
  getProjectList: (params) => api.get('/project', { params }),
  getProjectDetail: (id) => api.get(`/project/${id}`),
  createProject: (data) => api.post('/project', data),
  updateProject: (id, data) => api.put(`/project/${id}`, data),
  joinProject: (id) => api.post(`/project/${id}/join`),
  inviteUser: (id, data) => api.post(`/project/${id}/invite`, data),
  approveJoin: (id, userId) => api.post(`/project/${id}/approve/${userId}`)
}

// 文章相关接口
export const articleApi = {
  getArticleList: (params) => api.get('/article', { params }),
  getArticleDetail: (id) => api.get(`/article/${id}`),
  createArticle: (data) => api.post('/article', data),
  updateArticle: (id, data) => api.put(`/article/${id}`, data),
  deleteArticle: (id) => api.delete(`/article/${id}`)
}

// 管理员接口
export const adminApi = {
  getUserList: (params) => api.get('/admin/user', { params }),
  approveUser: (id) => api.post(`/admin/user/${id}/approve`),
  rejectUser: (id) => api.post(`/admin/user/${id}/reject`),
  disableUser: (id) => api.post(`/admin/user/${id}/disable`),
  enableUser: (id) => api.post(`/admin/user/${id}/enable`),
  createUser: (data) => api.post('/admin/user', data),
  
  getEquipmentApprovalList: (params) => api.get('/admin/equipment', { params }),
  approveEquipment: (id) => api.post(`/admin/equipment/${id}/approve`),
  rejectEquipment: (id) => api.post(`/admin/equipment/${id}/reject`),
  
  getProjectApprovalList: (params) => api.get('/admin/project', { params }),
  approveProject: (id) => api.post(`/admin/project/${id}/approve`),
  rejectProject: (id) => api.post(`/admin/project/${id}/reject`),
  
  getArticleApprovalList: (params) => api.get('/admin/article', { params }),
  approveArticle: (id) => api.post(`/admin/article/${id}/approve`),
  rejectArticle: (id) => api.post(`/admin/article/${id}/reject`)
}

export default api