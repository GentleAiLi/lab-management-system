<script setup>
import { ref, reactive, onMounted } from 'vue'
import { projectApi } from '../../api'
import { useRouter } from 'vue-router'

const router = useRouter()

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

// 创建项目对话框
const createDialogVisible = ref(false)
const projectForm = reactive({
  name: '',
  description: '',
  startDate: '',
  endDate: ''
})
const projectFormRef = ref(null)

// 项目表单验证规则
const projectRules = {
  name: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入项目描述', trigger: 'blur' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }]
}

// 加入项目对话框
const joinDialogVisible = ref(false)
const joinForm = reactive({
  projectId: '',
  reason: ''
})
const joinFormRef = ref(null)

// 加入项目表单验证规则
const joinRules = {
  projectId: [{ required: true, message: '请输入项目ID', trigger: 'blur' }],
  reason: [{ required: true, message: '请填写申请理由', trigger: 'blur' }]
}

// 获取项目列表
const fetchProjectList = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.currentPage,
      limit: pagination.pageSize,
      keyword: searchKeyword.value
    }
    
    const res = await projectApi.getProjectList(params)
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

// 打开创建项目对话框
const openCreateDialog = () => {
  projectForm.name = ''
  projectForm.description = ''
  projectForm.startDate = ''
  projectForm.endDate = ''
  createDialogVisible.value = true
}

// 提交创建项目
const submitCreateProject = async () => {
  if (!projectFormRef.value) return
  
  try {
    await projectFormRef.value.validate()
    
    await projectApi.createProject(projectForm)
    ElMessage.success('项目创建申请已提交，请等待审核')
    createDialogVisible.value = false
    fetchProjectList()
  } catch (error) {
    console.error('创建项目失败:', error)
    ElMessage.error('创建项目失败')
  }
}

// 打开加入项目对话框
const openJoinDialog = () => {
  joinForm.projectId = ''
  joinForm.reason = ''
  joinDialogVisible.value = true
}

// 提交加入项目申请
const submitJoinProject = async () => {
  if (!joinFormRef.value) return
  
  try {
    await joinFormRef.value.validate()
    
    await projectApi.joinProject(joinForm.projectId, { reason: joinForm.reason })
    ElMessage.success('加入项目申请已提交，请等待项目管理员审核')
    joinDialogVisible.value = false
    fetchProjectList()
  } catch (error) {
    console.error('申请加入项目失败:', error)
    ElMessage.error('申请加入项目失败')
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

// 获取项目角色显示文本
const getRoleText = (role) => {
  const roleMap = {
    owner: '创建者',
    member: '成员',
    pending: '申请中'
  }
  return roleMap[role] || '未知'
}

// 查看项目详情
const viewProjectDetail = (projectId) => {
  router.push(`/dashboard/project/${projectId}`)
}

// 页面加载时获取数据
onMounted(() => {
  fetchProjectList()
})
</script>

<template>
  <div class="project-page">
    <div class="page-header">
      <h2 class="page-title">项目管理</h2>
      <div class="action-buttons">
        <el-button type="primary" @click="openCreateDialog">
          <el-icon><Plus /></el-icon> 创建项目
        </el-button>
        <el-button type="success" @click="openJoinDialog">
          <el-icon><Connection /></el-icon> 加入项目
        </el-button>
      </div>
    </div>
    
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
    
    <el-tabs type="border-card" class="project-tabs">
      <el-tab-pane label="我的项目">
        <div v-loading="loading">
          <el-empty v-if="projectList.length === 0" description="暂无项目数据" />
          <div v-else class="project-list">
            <el-card 
              v-for="project in projectList" 
              :key="project.id"
              class="project-card"
              shadow="hover"
              @click="viewProjectDetail(project.id)"
            >
              <div class="project-card-header">
                <h3 class="project-name">{{ project.name }}</h3>
                <div class="project-tags">
                  <el-tag :type="getStatusType(project.status)" size="small">
                    {{ getStatusText(project.status) }}
                  </el-tag>
                  <el-tag type="info" size="small" class="role-tag">
                    {{ getRoleText(project.role) }}
                  </el-tag>
                </div>
              </div>
              <p class="project-description">{{ project.description }}</p>
              <div class="project-meta">
                <div class="meta-item">
                  <el-icon><Calendar /></el-icon>
                  <span>{{ project.startDate }} ~ {{ project.endDate }}</span>
                </div>
                <div class="meta-item">
                  <el-icon><User /></el-icon>
                  <span>{{ project.memberCount || 0 }}人</span>
                </div>
              </div>
            </el-card>
          </div>
          
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
        </div>
      </el-tab-pane>
    </el-tabs>
    
    <!-- 创建项目对话框 -->
    <el-dialog
      v-model="createDialogVisible"
      title="创建项目"
      width="600px"
    >
      <el-form
        ref="projectFormRef"
        :model="projectForm"
        :rules="projectRules"
        label-width="100px"
      >
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="projectForm.name" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="项目描述" prop="description">
          <el-input
            v-model="projectForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入项目描述"
          />
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker
            v-model="projectForm.startDate"
            type="date"
            placeholder="选择开始日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker
            v-model="projectForm.endDate"
            type="date"
            placeholder="选择结束日期"
            style="width: 100%"
            :disabled-date="date => !projectForm.startDate || date < projectForm.startDate"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitCreateProject">提交申请</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 加入项目对话框 -->
    <el-dialog
      v-model="joinDialogVisible"
      title="加入项目"
      width="500px"
    >
      <el-form
        ref="joinFormRef"
        :model="joinForm"
        :rules="joinRules"
        label-width="100px"
      >
        <el-form-item label="项目ID" prop="projectId">
          <el-input v-model="joinForm.projectId" placeholder="请输入项目ID" />
        </el-form-item>
        <el-form-item label="申请理由" prop="reason">
          <el-input
            v-model="joinForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请简要说明申请理由"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="joinDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitJoinProject">提交申请</el-button>
        </span>
      </template>
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

.search-box {
  margin-bottom: 20px;
  max-width: 400px;
}

.project-tabs {
  margin-top: 20px;
}

.project-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.project-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.project-card:hover {
  transform: translateY(-5px);
}

.project-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.project-name {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.project-tags {
  display: flex;
  gap: 5px;
}

.role-tag {
  margin-left: 5px;
}

.project-description {
  color: #666;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.project-meta {
  display: flex;
  justify-content: space-between;
  color: #999;
  font-size: 14px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>