import { defineStore } from 'pinia'
import { userApi } from '../api'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}')
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.userInfo.role === 'admin',
    username: (state) => state.userInfo.username || ''
  },
  actions: {
    setUserInfo(userInfo) {
      this.userInfo = userInfo
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    },
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },
    async refreshToken() {
      try {
        const response = await userApi.refreshToken()
        if (response.code === 1 && response.data) {
          // 根据新的API响应格式处理数据
          const { accessToken, accountName, id, role } = response.data
          // 更新token
          this.setToken(accessToken)
          // 更新用户信息
          this.setUserInfo({
            id,
            username: accountName,
            role: role === 1 ? 'admin' : 'user'
          })
          return true
        }
        return false
      } catch (error) {
        console.error('刷新令牌失败:', error)
        return false
      }
    },
    async logout() {
      try {
        // 调用登出API
        await userApi.logout()
      } catch (error) {
        console.error('登出失败:', error)
      } finally {
        // 无论API调用成功与否，都清除本地状态
        this.token = ''
        this.userInfo = {}
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
      }
    }
  }
})