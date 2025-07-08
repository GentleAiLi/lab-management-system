<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()

// 侧边栏折叠状态
const isCollapse = ref(false)

// 管理员菜单
const adminMenuItems = [
  {
    icon: 'DataBoard',
    title: '管理概览',
    path: '/admin/overview'
  },
  {
    icon: 'User',
    title: '用户管理',
    path: '/admin/users'
  },
  {
    icon: 'Monitor',
    title: '设备审核',
    path: '/admin/equipment'
  },
  {
    icon: 'Connection',
    title: '项目审核',
    path: '/admin/projects'
  },
  {
    icon: 'Document',
    title: '文章审核',
    path: '/admin/articles'
  }
]

// 当前激活的菜单项
const activeMenu = computed(() => {
  return router.currentRoute.value.path
})

// 用户名
const username = computed(() => userStore.username)

// 返回用户端
const goToDashboard = () => {
  router.push('/dashboard')
}

// 处理退出登录
const handleLogout = () => {
  userStore.logout()
  router.push('/')
}

// 返回首页
const goToHome = () => {
  router.push('/')
}
</script>

<template>
  <div class="admin-layout">
    <!-- 顶部导航栏 -->
    <el-header class="admin-header">
      <div class="header-left">
        <el-button link class="toggle-btn" @click="isCollapse = !isCollapse">
          <el-icon>
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
        </el-button>
        <div class="logo" @click="goToHome">
          <span class="admin-badge">管理端</span>
          AI实验室管理系统
        </div>
      </div>

      <div class="header-right">
        <el-button type="info" @click="goToDashboard">
          <el-icon>
            <Back />
          </el-icon>
          返回用户端
        </el-button>

        <el-dropdown trigger="click">
          <div class="user-info">
            <el-avatar :size="32" icon="UserFilled" />
            <span class="username">{{ username }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item divided @click="handleLogout">
                <el-icon>
                  <SwitchButton />
                </el-icon> 退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <div class="admin-container">
      <!-- 侧边栏 -->
      <el-aside width="auto" class="admin-aside">
        <el-menu :default-active="activeMenu" class="admin-menu" :collapse="isCollapse" :collapse-transition="false"
          router background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF">
          <el-menu-item v-for="item in adminMenuItems" :key="item.path" :index="item.path">
            <el-icon>
              <component :is="item.icon" />
            </el-icon>
            <template #title>{{ item.title }}</template>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </div>
  </div>
</template>

<style scoped>
.admin-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.admin-header {
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
  display: flex;
  align-items: center;
}

.admin-badge {
  background-color: #f56c6c;
  color: white;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
  margin-right: 10px;
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

.admin-container {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.admin-aside {
  background-color: #304156;
  transition: width 0.3s;
}

.admin-menu {
  height: 100%;
  border-right: none;
}

.admin-menu:not(.el-menu--collapse) {
  width: 200px;
}

.admin-main {
  background-color: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}
</style>