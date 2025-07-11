import api from './request'

// 文章相关接口
export const articleApi = {
  getArticleList: (params) => api.get('/article', { params }),
  getArticleDetail: (id) => api.get(`/article/${id}`),
  createArticle: (data) => api.post('/article', data),
  updateArticle: (id, data) => api.put(`/article/${id}`, data),
  deleteArticle: (id) => api.delete(`/article/${id}`)
}

export default articleApi