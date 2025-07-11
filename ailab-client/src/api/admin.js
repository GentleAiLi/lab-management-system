import api from './request'

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

export default adminApi