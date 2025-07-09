import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/dashboard/Layout.vue'),
    redirect: '/dashboard/overview',
    meta: { requiresAuth: true, title: '用户中心' },
    children: [
      {
        path: 'overview',
        name: 'Overview',
        component: () => import('../views/dashboard/Overview.vue'),
        meta: { title: '概览' }
      },
      {
        path: 'equipment',
        name: 'Equipment',
        component: () => import('../views/dashboard/Equipment.vue'),
        meta: { title: '设备管理' }
      },
      {
        path: 'projects',
        name: 'Projects',
        component: () => import('../views/dashboard/Project.vue'),
        meta: { title: '项目管理' }
      },
      {
        path: 'articles',
        name: 'Articles',
        component: () => import('../views/dashboard/Article.vue'),
        meta: { title: '文章管理' }
      },
      {
        path: 'article/edit/:id?',
        name: 'ArticleEdit',
        component: () => import('../views/dashboard/ArticleEdit.vue'),
        meta: { title: '编辑文章' }
      }
    ]
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/admin/Layout.vue'),
    redirect: '/admin/overview',
    meta: { requiresAuth: true, requiresAdmin: true, title: '管理中心' },
    children: [
      {
        path: 'overview',
        name: 'AdminOverview',
        component: () => import('../views/admin/Overview.vue'),
        meta: { title: '管理概览' }
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('../views/admin/User.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'equipment',
        name: 'EquipmentManagement',
        component: () => import('../views/admin/Equipment.vue'),
        meta: { title: '设备审核' }
      },
      {
        path: 'projects',
        name: 'ProjectManagement',
        component: () => import('../views/admin/Project.vue'),
        meta: { title: '项目审核' }
      },
      {
        path: 'articles',
        name: 'ArticleManagement',
        component: () => import('../views/admin/Article.vue'),
        meta: { title: '文章审核' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue'),
    meta: { title: '页面未找到' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫配置

// 全局前置守卫
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 实验室管理系统` : '实验室管理系统'
  
  // 检查是否需要登录权限
  const isAuthenticated = localStorage.getItem('token')
  
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!isAuthenticated) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else {
      // 检查是否需要管理员权限
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      if (to.matched.some(record => record.meta.requiresAdmin) && userInfo.role !== 'admin') {
        next({ path: '/dashboard' })
      } else {
        // 如果令牌即将过期，尝试刷新令牌
        // 这里可以添加令牌过期时间的检查逻辑
        // 为简化实现，此处不检查过期时间，仅作为示例
        // const tokenExpiresSoon = checkIfTokenExpiresSoon()
        // if (tokenExpiresSoon) {
        //   const userStore = useUserStore()
        //   await userStore.refreshToken()
        // }
        next()
      }
    }
  } else {
    next()
  }
})

// 全局后置钩子
router.afterEach(() => {
  // 可以在这里添加页面加载完成后的逻辑
})

export default router