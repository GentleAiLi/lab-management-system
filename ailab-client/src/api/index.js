/**
 * API模块统一导出入口
 */

// 导入基础请求实例
import api from './request'

// 导入各个模块API
import { userApi } from './user'
import { equipmentApi } from './equipment'
import { projectApi } from './project'
import { articleApi } from './article'
import { adminApi } from './admin'

// 统一导出
export {
  api,
  userApi,
  equipmentApi,
  projectApi,
  articleApi,
  adminApi
}

// 默认导出请求实例
export default api