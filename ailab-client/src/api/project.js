import api from './request'

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

export default projectApi