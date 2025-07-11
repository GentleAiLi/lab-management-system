import api from './request'

// 设备相关接口
export const equipmentApi = {
  getEquipmentList: (params) => api.get('/equipment', { params }),
  getEquipmentDetail: (id) => api.get(`/equipment/${id}`),
  borrowEquipment: (id, data) => api.post(`/equipment/${id}/borrow`, data),
  returnEquipment: (id) => api.post(`/equipment/${id}/return`)
}

export default equipmentApi