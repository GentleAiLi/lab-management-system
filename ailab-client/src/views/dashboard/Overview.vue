<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../../stores/user'
import { equipmentApi, projectApi, articleApi } from '../../api'

const userStore = useUserStore()

// 用户信息
const userInfo = ref({
  username: userStore.username,
  name: userStore.userInfo.name || '',
  email: userStore.userInfo.email || '',
  role: userStore.userInfo.role === 'admin' ? '管理员' : '普通用户',
  joinTime: userStore.userInfo.createdAt ? new Date(userStore.userInfo.createdAt).toLocaleDateString() : '未知'
})

// 统计数据
const statistics = ref({
  equipmentCount: 0,
  projectCount: 0,
  articleCount: 0,
  pendingEquipmentCount: 0,
  pendingProjectCount: 0
})

// 最近借用的设备
const recentEquipments = ref([])

// 参与的项目
const projects = ref([])

// 最近的文章
const recentArticles = ref([])

// 加载状态
const loading = ref({
  equipment: false,
  project: false,
  article: false
})

// 获取设备数据
const fetchEquipmentData = async () => {
  try {
    loading.value.equipment = true
    const res = await equipmentApi.getEquipmentList({ limit: 5, status: 'borrowed' })
    recentEquipments.value = res.data || []
    statistics.value.equipmentCount = res.total || 0
    statistics.value.pendingEquipmentCount = res.pending || 0
  } catch (error) {
    console.error('获取设备数据失败:', error)
  } finally {
    loading.value.equipment = false
  }
}

// 获取项目数据
const fetchProjectData = async () => {
  try {
    loading.value.project = true
    const res = await projectApi.getProjectList({ limit: 5 })
    projects.value = res.data || []
    statistics.value.projectCount = res.total || 0
    statistics.value.pendingProjectCount = res.pending || 0
  } catch (error) {
    console.error('获取项目数据失败:', error)
  } finally {
    loading.value.project = false
  }
}

// 获取文章数据
const fetchArticleData = async () => {
  try {
    loading.value.article = true
    const res = await articleApi.getArticleList({ limit: 5 })
    recentArticles.value = res.data || []
    statistics.value.articleCount = res.total || 0
  } catch (error) {
    console.error('获取文章数据失败:', error)
  } finally {
    loading.value.article = false
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchEquipmentData()
  fetchProjectData()
  fetchArticleData()
})
</script>

<template>
  <div class="overview-page">
    <h2 class="page-title">用户中心</h2>
    
    <!-- 用户信息卡片 -->
    <el-card class="user-info-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
        </div>
      </template>
      <div class="user-info-content">
        <el-avatar :size="80" icon="UserFilled" class="user-avatar" />
        <div class="user-details">
          <div class="user-detail-item">
            <span class="label">用户名:</span>
            <span class="value">{{ userInfo.username }}</span>
          </div>
          <div class="user-detail-item">
            <span class="label">姓名:</span>
            <span class="value">{{ userInfo.name }}</span>
          </div>
          <div class="user-detail-item">
            <span class="label">邮箱:</span>
            <span class="value">{{ userInfo.email }}</span>
          </div>
          <div class="user-detail-item">
            <span class="label">角色:</span>
            <span class="value">{{ userInfo.role }}</span>
          </div>
          <div class="user-detail-item">
            <span class="label">加入时间:</span>
            <span class="value">{{ userInfo.joinTime }}</span>
          </div>
        </div>
      </div>
    </el-card>
    
    <!-- 统计数据 -->
    <div class="statistics-grid">
      <el-card class="statistic-card">
        <el-statistic title="我的设备" :value="statistics.equipmentCount">
          <template #icon>
            <el-icon><Monitor /></el-icon>
          </template>
        </el-statistic>
        <div class="statistic-footer">
          <span>待审核: {{ statistics.pendingEquipmentCount }}</span>
        </div>
      </el-card>
      
      <el-card class="statistic-card">
        <el-statistic title="我的项目" :value="statistics.projectCount">
          <template #icon>
            <el-icon><Connection /></el-icon>
          </template>
        </el-statistic>
        <div class="statistic-footer">
          <span>待审核: {{ statistics.pendingProjectCount }}</span>
        </div>
      </el-card>
      
      <el-card class="statistic-card">
        <el-statistic title="我的文章" :value="statistics.articleCount">
          <template #icon>
            <el-icon><Document /></el-icon>
          </template>
        </el-statistic>
      </el-card>
    </div>
    
    <!-- 最近借用的设备 -->
    <el-card class="data-card">
      <template #header>
        <div class="card-header">
          <span>最近借用的设备</span>
          <el-button link @click="$router.push('/dashboard/equipment')">
            查看全部
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </template>
      <div v-loading="loading.equipment">
        <el-empty v-if="recentEquipments.length === 0" description="暂无借用设备" />
        <el-table v-else :data="recentEquipments" style="width: 100%">
          <el-table-column prop="name" label="设备名称" />
          <el-table-column prop="borrowTime" label="借用时间" />
          <el-table-column prop="returnTime" label="预计归还时间" />
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="scope.row.status === 'borrowed' ? 'success' : 'info'">
                {{ scope.row.status === 'borrowed' ? '已借用' : '已归还' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
    
    <!-- 参与的项目 -->
    <el-card class="data-card">
      <template #header>
        <div class="card-header">
          <span>参与的项目</span>
          <el-button link @click="$router.push('/dashboard/projects')">
            查看全部
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </template>
      <div v-loading="loading.project">
        <el-empty v-if="projects.length === 0" description="暂无参与项目" />
        <el-table v-else :data="projects" style="width: 100%">
          <el-table-column prop="name" label="项目名称" />
          <el-table-column prop="leader" label="项目负责人" />
          <el-table-column prop="memberCount" label="成员数" />
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="scope.row.status === 'approved' ? 'success' : 'warning'">
                {{ scope.row.status === 'approved' ? '已批准' : '审核中' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
    
    <!-- 最近的文章 -->
    <el-card class="data-card">
      <template #header>
        <div class="card-header">
          <span>最近的文章</span>
          <el-button link @click="$router.push('/dashboard/articles')">
            查看全部
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </template>
      <div v-loading="loading.article">
        <el-empty v-if="recentArticles.length === 0" description="暂无文章" />
        <el-table v-else :data="recentArticles" style="width: 100%">
          <el-table-column prop="title" label="文章标题" />
          <el-table-column prop="createdAt" label="发布时间" />
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="scope.row.status === 'published' ? 'success' : 'warning'">
                {{ scope.row.status === 'published' ? '已发布' : '审核中' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button link @click="$router.push(`/dashboard/article/edit/${scope.row.id}`)">
                编辑
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.overview-page {
  padding-bottom: 30px;
}

.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  color: #333;
}

.user-info-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info-content {
  display: flex;
  align-items: flex-start;
}

.user-avatar {
  margin-right: 30px;
}

.user-details {
  flex: 1;
}

.user-detail-item {
  margin-bottom: 10px;
  display: flex;
}

.user-detail-item .label {
  width: 100px;
  color: #909399;
}

.user-detail-item .value {
  font-weight: 500;
}

.statistics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.statistic-card {
  padding: 10px;
}

.statistic-footer {
  margin-top: 10px;
  font-size: 14px;
  color: #909399;
}

.data-card {
  margin-bottom: 20px;
}

.el-empty {
  padding: 30px 0;
}
</style>