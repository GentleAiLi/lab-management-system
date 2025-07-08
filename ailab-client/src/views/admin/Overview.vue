<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { adminApi } from '../../api'

const router = useRouter()

// 统计数据
const statistics = reactive({
  userCount: 0,
  equipmentCount: 0,
  projectCount: 0,
  articleCount: 0,
  pendingUserCount: 0,
  pendingEquipmentCount: 0,
  pendingProjectCount: 0,
  pendingArticleCount: 0
})

// 最近注册的用户
const recentUsers = ref([])

// 最近的设备申请
const recentEquipmentRequests = ref([])

// 最近的项目申请
const recentProjectRequests = ref([])

// 最近的文章提交
const recentArticles = ref([])

// 加载状态
const loading = ref(true)

// 获取管理员概览数据
const fetchOverviewData = async () => {
  try {
    loading.value = true
    const res = await adminApi.getAdminOverview()
    
    // 更新统计数据
    if (res.statistics) {
      Object.assign(statistics, res.statistics)
    }
    
    // 更新最近数据
    recentUsers.value = res.recentUsers || []
    recentEquipmentRequests.value = res.recentEquipmentRequests || []
    recentProjectRequests.value = res.recentProjectRequests || []
    recentArticles.value = res.recentArticles || []
  } catch (error) {
    console.error('获取管理员概览数据失败:', error)
    ElMessage.error('获取管理员概览数据失败')
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取用户状态标签类型
const getUserStatusType = (status) => {
  const statusMap = {
    pending: 'warning',
    active: 'success',
    disabled: 'info'
  }
  return statusMap[status] || ''
}

// 获取用户状态显示文本
const getUserStatusText = (status) => {
  const statusMap = {
    pending: '待审核',
    active: '正常',
    disabled: '已禁用'
  }
  return statusMap[status] || '未知'
}

// 获取设备申请状态标签类型
const getEquipmentRequestStatusType = (status) => {
  const statusMap = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger',
    returned: 'info'
  }
  return statusMap[status] || ''
}

// 获取设备申请状态显示文本
const getEquipmentRequestStatusText = (status) => {
  const statusMap = {
    pending: '待审核',
    approved: '已批准',
    rejected: '已拒绝',
    returned: '已归还'
  }
  return statusMap[status] || '未知'
}

// 获取项目状态标签类型
const getProjectStatusType = (status) => {
  const statusMap = {
    pending: 'warning',
    active: 'success',
    rejected: 'danger'
  }
  return statusMap[status] || ''
}

// 获取项目状态显示文本
const getProjectStatusText = (status) => {
  const statusMap = {
    pending: '待审核',
    active: '进行中',
    rejected: '已拒绝'
  }
  return statusMap[status] || '未知'
}

// 获取文章状态标签类型
const getArticleStatusType = (status) => {
  const statusMap = {
    draft: '',
    pending: 'warning',
    published: 'success',
    rejected: 'danger'
  }
  return statusMap[status] || ''
}

// 获取文章状态显示文本
const getArticleStatusText = (status) => {
  const statusMap = {
    draft: '草稿',
    pending: '待审核',
    published: '已发布',
    rejected: '已拒绝'
  }
  return statusMap[status] || '未知'
}

// 导航到用户管理页面
const navigateToUserManagement = () => {
  router.push('/admin/user')
}

// 导航到设备管理页面
const navigateToEquipmentManagement = () => {
  router.push('/admin/equipment')
}

// 导航到项目管理页面
const navigateToProjectManagement = () => {
  router.push('/admin/project')
}

// 导航到文章管理页面
const navigateToArticleManagement = () => {
  router.push('/admin/article')
}

// 页面加载时获取数据
onMounted(() => {
  fetchOverviewData()
})
</script>

<template>
  <div class="admin-overview" v-loading="loading">
    <div class="page-header">
      <h2 class="page-title">管理员概览</h2>
    </div>
    
    <!-- 统计卡片 -->
    <div class="statistics-container">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="statistic-card" shadow="hover" @click="navigateToUserManagement">
            <div class="statistic-content">
              <div class="statistic-icon user-icon">
                <el-icon><User /></el-icon>
              </div>
              <div class="statistic-info">
                <div class="statistic-title">用户</div>
                <div class="statistic-value">{{ statistics.userCount }}</div>
                <div class="statistic-extra" v-if="statistics.pendingUserCount > 0">
                  <el-tag type="warning" size="small">
                    {{ statistics.pendingUserCount }} 待审核
                  </el-tag>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card class="statistic-card" shadow="hover" @click="navigateToEquipmentManagement">
            <div class="statistic-content">
              <div class="statistic-icon equipment-icon">
                <el-icon><Monitor /></el-icon>
              </div>
              <div class="statistic-info">
                <div class="statistic-title">设备</div>
                <div class="statistic-value">{{ statistics.equipmentCount }}</div>
                <div class="statistic-extra" v-if="statistics.pendingEquipmentCount > 0">
                  <el-tag type="warning" size="small">
                    {{ statistics.pendingEquipmentCount }} 待审核
                  </el-tag>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card class="statistic-card" shadow="hover" @click="navigateToProjectManagement">
            <div class="statistic-content">
              <div class="statistic-icon project-icon">
                <el-icon><Folder /></el-icon>
              </div>
              <div class="statistic-info">
                <div class="statistic-title">项目</div>
                <div class="statistic-value">{{ statistics.projectCount }}</div>
                <div class="statistic-extra" v-if="statistics.pendingProjectCount > 0">
                  <el-tag type="warning" size="small">
                    {{ statistics.pendingProjectCount }} 待审核
                  </el-tag>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card class="statistic-card" shadow="hover" @click="navigateToArticleManagement">
            <div class="statistic-content">
              <div class="statistic-icon article-icon">
                <el-icon><Document /></el-icon>
              </div>
              <div class="statistic-info">
                <div class="statistic-title">文章</div>
                <div class="statistic-value">{{ statistics.articleCount }}</div>
                <div class="statistic-extra" v-if="statistics.pendingArticleCount > 0">
                  <el-tag type="warning" size="small">
                    {{ statistics.pendingArticleCount }} 待审核
                  </el-tag>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <!-- 待处理事项 -->
    <div class="pending-items-container">
      <el-row :gutter="20">
        <!-- 最近注册的用户 -->
        <el-col :span="12">
          <el-card class="pending-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>最近注册的用户</span>
                <el-button 
                  type="primary" 
                  size="small" 
                  plain
                  @click="navigateToUserManagement"
                >
                  查看全部
                </el-button>
              </div>
            </template>
            <el-empty v-if="recentUsers.length === 0" description="暂无数据" />
            <el-table v-else :data="recentUsers" style="width: 100%">
              <el-table-column prop="username" label="用户名" width="120" />
              <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
              <el-table-column label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="getUserStatusType(scope.row.status)">
                    {{ getUserStatusText(scope.row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="注册时间" width="180">
                <template #default="scope">
                  {{ formatDate(scope.row.createdAt) }}
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
        
        <!-- 最近的设备申请 -->
        <el-col :span="12">
          <el-card class="pending-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>最近的设备申请</span>
                <el-button 
                  type="primary" 
                  size="small" 
                  plain
                  @click="navigateToEquipmentManagement"
                >
                  查看全部
                </el-button>
              </div>
            </template>
            <el-empty v-if="recentEquipmentRequests.length === 0" description="暂无数据" />
            <el-table v-else :data="recentEquipmentRequests" style="width: 100%">
              <el-table-column prop="equipmentName" label="设备名称" min-width="150" show-overflow-tooltip />
              <el-table-column prop="username" label="申请人" width="120" />
              <el-table-column label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="getEquipmentRequestStatusType(scope.row.status)">
                    {{ getEquipmentRequestStatusText(scope.row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="申请时间" width="180">
                <template #default="scope">
                  {{ formatDate(scope.row.createdAt) }}
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" class="second-row">
        <!-- 最近的项目申请 -->
        <el-col :span="12">
          <el-card class="pending-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>最近的项目申请</span>
                <el-button 
                  type="primary" 
                  size="small" 
                  plain
                  @click="navigateToProjectManagement"
                >
                  查看全部
                </el-button>
              </div>
            </template>
            <el-empty v-if="recentProjectRequests.length === 0" description="暂无数据" />
            <el-table v-else :data="recentProjectRequests" style="width: 100%">
              <el-table-column prop="name" label="项目名称" min-width="150" show-overflow-tooltip />
              <el-table-column prop="creator" label="创建人" width="120" />
              <el-table-column label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="getProjectStatusType(scope.row.status)">
                    {{ getProjectStatusText(scope.row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="申请时间" width="180">
                <template #default="scope">
                  {{ formatDate(scope.row.createdAt) }}
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
        
        <!-- 最近的文章提交 -->
        <el-col :span="12">
          <el-card class="pending-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>最近的文章提交</span>
                <el-button 
                  type="primary" 
                  size="small" 
                  plain
                  @click="navigateToArticleManagement"
                >
                  查看全部
                </el-button>
              </div>
            </template>
            <el-empty v-if="recentArticles.length === 0" description="暂无数据" />
            <el-table v-else :data="recentArticles" style="width: 100%">
              <el-table-column prop="title" label="标题" min-width="150" show-overflow-tooltip />
              <el-table-column prop="author" label="作者" width="120" />
              <el-table-column label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="getArticleStatusType(scope.row.status)">
                    {{ getArticleStatusText(scope.row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="提交时间" width="180">
                <template #default="scope">
                  {{ formatDate(scope.row.createdAt) }}
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<style scoped>
.admin-overview {
  padding-bottom: 30px;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  color: #333;
  margin: 0;
}

.statistics-container {
  margin-bottom: 20px;
}

.statistic-card {
  height: 120px;
  cursor: pointer;
  transition: transform 0.3s;
}

.statistic-card:hover {
  transform: translateY(-5px);
}

.statistic-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.statistic-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  margin-right: 15px;
  font-size: 24px;
  color: white;
}

.user-icon {
  background-color: #409EFF;
}

.equipment-icon {
  background-color: #67C23A;
}

.project-icon {
  background-color: #E6A23C;
}

.article-icon {
  background-color: #F56C6C;
}

.statistic-info {
  flex: 1;
}

.statistic-title {
  font-size: 16px;
  color: #606266;
  margin-bottom: 5px;
}

.statistic-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.statistic-extra {
  margin-top: 5px;
}

.pending-items-container {
  margin-top: 20px;
}

.second-row {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pending-card {
  margin-bottom: 0;
}
</style>