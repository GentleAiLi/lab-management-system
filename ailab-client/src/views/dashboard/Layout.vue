<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()

// 侧边栏折叠状态
const isCollapse = ref(false)

// 用户菜单
const userMenuItems = [
  {
    icon: 'DataBoard',
    title: '概览',
    path: '/dashboard/overview'
  },
  {
    icon: 'Monitor',
    title: '设备管理',
    path: '/dashboard/equipment'
  },
  {
    icon: 'Connection',
    title: '项目管理',
    path: '/dashboard/projects'
  },
  {
    icon: 'Document',
    title: '文章管理',
    path: '/dashboard/articles'
  }
]

// 当前激活的菜单项
const activeMenu = computed(() => {
  return router.currentRoute.value.path
})

// 用户名
const username = computed(() => userStore.username)

// 是否为管理员
const isAdmin = computed(() => userStore.isAdmin)

// 处理退出登录
const handleLogout = () => {
  userStore.logout()
  router.push('/')
}

// 进入管理端
const goToAdmin = () => {
  router.push('/admin')
}

// 返回首页
const goToHome = () => {
  router.push('/')
}
</script>

<template>
  <div class="dashboard-layout">
    <!-- 顶部导航栏 -->
    <el-header class="dashboard-header">
      <div class="header-left">
        <el-button 
          link 
          class="toggle-btn"
          @click="isCollapse = !isCollapse"
        >
          <el-icon>
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
        </el-button>
        <div class="logo" @click="goToHome">AI实验室管理系统</div>
      </div>
      
      <div class="header-right">
        <el-button v-if="isAdmin" type="primary" @click="goToAdmin">
          <el-icon><Setting /></el-icon>
          进入管理端
        </el-button>
        
        <el-dropdown trigger="click">
          <div class="user-info">
            <el-avatar :size="32" icon="UserFilled" />
            <span class="username">{{ username }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="router.push('/dashboard/overview')">
                <el-icon><User /></el-icon> 个人中心
              </el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">
                <el-icon><SwitchButton /></el-icon> 退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    
    <div class="dashboard-container">
      <!-- 侧边栏 -->
      <el-aside width="auto" class="dashboard-aside">
        <el-menu
          :default-active="activeMenu"
          class="dashboard-menu"
          :collapse="isCollapse"
          :collapse-transition="false"
          router
        >
          <el-menu-item 
            v-for="item in userMenuItems" 
            :key="item.path" 
            :index="item.path"
          >
            <el-icon><component :is="item.icon" /></el-icon>
            <template #title>{{ item.title }}</template>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <!-- 主内容区 -->
      <el-main class="dashboard-main">
        <router-view />
      </el-main>
    </div>
  </div>
</template>

<style scoped>
.dashboard-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.dashboard-header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
}

.header-left {
  display: flex;
  align-items: center;
}

.toggle-btn {
  margin-right: 15px;
  font-size: 20px;
}

.logo {
  font-size: 18px;
  font-weight: bold;
  color: #409EFF;
  cursor: pointer;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  margin-left: 20px;
}

.username {
  margin-left: 10px;
  font-size: 14px;
}

.dashboard-container {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.dashboard-aside {
  background-color: #fff;
  border-right: 1px solid #e6e6e6;
  transition: width 0.3s;
}

.dashboard-menu {
  height: 100%;
  border-right: none;
}

.dashboard-menu:not(.el-menu--collapse) {
  width: 200px;
}

.dashboard-main {
  background-color: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}
</style>