import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000
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
  error => {
    if (error.response && error.response.status === 401) {
      // 未授权，清除token并跳转到登录页
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
  updateUserInfo: (data) => api.put('/user/info', data)
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