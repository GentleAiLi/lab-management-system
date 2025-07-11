import api from './request'

// 用户相关接口
export const userApi = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/user/register', data),
  getUserInfo: () => api.get('/user/info'),
  updateUserInfo: (data) => api.put('/user/info', data),
  logout: () => api.get('/auth/logout'),
  refreshToken: () => api.get('/auth/refresh')
}

export default userApi