<script setup>
import { ref, reactive, onMounted } from 'vue'
import { adminApi } from '../../api'
import { ElMessage } from 'element-plus'


// 项目列表
const projectList = ref([])

// 分页参数
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 加载状态
const loading = ref(false)

// 搜索关键词
const searchKeyword = ref('')

// 筛选状态
const filterStatus = ref('all')

// 项目详情对话框
const detailDialogVisible = ref(false)
const currentProject = ref(null)

// 获取项目列表
const fetchProjectList = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.currentPage,
      limit: pagination.pageSize,
      keyword: searchKeyword.value,
      status: filterStatus.value !== 'all' ? filterStatus.value : undefined
    }
    
    const res = await adminApi.getProjectList(params)
    projectList.value = res.data || []
    pagination.total = res.total || 0
  } catch (error) {
    console.error('获取项目列表失败:', error)
    ElMessage.error('获取项目列表失败')
  } finally {
    loading.value = false
  }
}

// 处理页码变化
const handleCurrentChange = (page) => {
  pagination.currentPage = page
  fetchProjectList()
}

// 处理每页条数变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  fetchProjectList()
}

// 处理搜索
const handleSearch = () => {
  pagination.currentPage = 1
  fetchProjectList()
}

// 处理状态筛选
const handleStatusChange = () => {
  pagination.currentPage = 1
  fetchProjectList()
}

// 查看项目详情
const viewProjectDetail = (project) => {
  currentProject.value = project
  detailDialogVisible.value = true
}

// 处理项目申请
const handleProjectApplication = async (projectId, status, reason = '') => {
  try {
    await adminApi.handleProjectApplication(projectId, { status, reason })
    ElMessage.success(status === 'approved' ? '已批准项目申请' : '已拒绝项目申请')
    fetchProjectList()
    if (detailDialogVisible.value) {
      detailDialogVisible.value = false
    }
  } catch (error) {
    console.error('处理项目申请失败:', error)
    ElMessage.error('处理项目申请失败')
  }
}

// 拒绝项目申请（带原因）
const rejectProjectWithReason = async (projectId) => {
  try {
    const { value: reason } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝项目申请', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '请输入拒绝原因'
    })
    
    if (reason) {
      await handleProjectApplication(projectId, 'rejected', reason)
    }
  } catch (error) {
    // 用户取消输入
  }
}

// 获取项目状态标签类型
const getStatusType = (status) => {
  const statusMap = {
    pending: 'info',
    active: 'success',
    completed: 'warning',
    rejected: 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取项目状态显示文本
const getStatusText = (status) => {
  const statusMap = {
    pending: '审核中',
    active: '进行中',
    completed: '已完成',
    rejected: '已拒绝'
  }
  return statusMap[status] || '未知'
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit'
  })
}

// 页面加载时获取数据
onMounted(() => {
  fetchProjectList()
})
</script>

<template>
  <div class="project-page">
    <div class="page-header">
      <h2 class="page-title">项目审核</h2>
    </div>
    
    <div class="filter-container">
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索项目名称"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>
      
      <div class="status-filter">
        <el-select v-model="filterStatus" placeholder="项目状态" @change="handleStatusChange">
          <el-option label="全部" value="all" />
          <el-option label="审核中" value="pending" />
          <el-option label="进行中" value="active" />
          <el-option label="已完成" value="completed" />
          <el-option label="已拒绝" value="rejected" />
        </el-select>
      </div>
    </div>
    
    <el-card v-loading="loading">
      <el-empty v-if="projectList.length === 0" description="暂无项目数据" />
      <el-table v-else :data="projectList" style="width: 100%">
        <el-table-column prop="name" label="项目名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="owner" label="创建者" width="120" />
        <el-table-column label="项目周期" width="200">
          <template #default="scope">
            {{ formatDate(scope.row.startDate) }} ~ {{ formatDate(scope.row.endDate) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="memberCount" label="成员数" width="100" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button 
              type="primary" 
              size="small"
              @click="viewProjectDetail(scope.row)"
            >
              详情
            </el-button>
            <template v-if="scope.row.status === 'pending'">
              <el-button 
                type="success" 
                size="small"
                @click="handleProjectApplication(scope.row.id, 'approved')"
              >
                批准
              </el-button>
              <el-button 
                type="danger" 
                size="small"
                @click="rejectProjectWithReason(scope.row.id)"
              >
                拒绝
              </el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 项目详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="项目详情"
      width="700px"
    >
      <div v-if="currentProject" class="project-detail">
        <div class="detail-header">
          <h3 class="detail-title">{{ currentProject.name }}</h3>
          <el-tag :type="getStatusType(currentProject.status)">
            {{ getStatusText(currentProject.status) }}
          </el-tag>
        </div>
        
        <div class="detail-content">
          <div class="detail-item">
            <span class="label">项目ID:</span>
            <span class="value">{{ currentProject.id }}</span>
          </div>
          <div class="detail-item">
            <span class="label">创建者:</span>
            <span class="value">{{ currentProject.owner }}</span>
          </div>
          <div class="detail-item">
            <span class="label">开始日期:</span>
            <span class="value">{{ formatDate(currentProject.startDate) }}</span>
          </div>
          <div class="detail-item">
            <span class="label">结束日期:</span>
            <span class="value">{{ formatDate(currentProject.endDate) }}</span>
          </div>
          <div class="detail-item">
            <span class="label">创建时间:</span>
            <span class="value">{{ currentProject.createdAt }}</span>
          </div>
          <div class="detail-item">
            <span class="label">成员数:</span>
            <span class="value">{{ currentProject.memberCount || 0 }}</span>
          </div>
          <div class="detail-item full-width">
            <span class="label">项目描述:</span>
            <div class="description-content">{{ currentProject.description }}</div>
          </div>
        </div>
        
        <div class="detail-actions" v-if="currentProject.status === 'pending'">
          <el-button 
            type="success" 
            @click="handleProjectApplication(currentProject.id, 'approved')"
          >
            批准项目
          </el-button>
          <el-button 
            type="danger" 
            @click="rejectProjectWithReason(currentProject.id)"
          >
            拒绝项目
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.project-page {
  padding-bottom: 30px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  color: #333;
  margin: 0;
}

.filter-container {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.search-box {
  flex: 1;
  max-width: 400px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.project-detail {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.detail-title {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.detail-content {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.detail-item {
  width: calc(50% - 15px);
  display: flex;
  flex-direction: column;
}

.detail-item.full-width {
  width: 100%;
}

.label {
  font-weight: bold;
  color: #666;
  margin-bottom: 5px;
}

.value {
  color: #333;
}

.description-content {
  background-color: #f9f9f9;
  padding: 10px;
  border-radius: 4px;
  white-space: pre-line;
}

.detail-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}
</style>