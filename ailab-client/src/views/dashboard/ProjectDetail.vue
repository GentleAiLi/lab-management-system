<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { projectApi } from '../../api'
import { useUserStore } from '../../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 项目ID
const projectId = computed(() => route.params.id)

// 项目详情
const projectDetail = ref(null)

// 加载状态
const loading = ref(false)

// 成员列表
const memberList = ref([])

// 邀请成员对话框
const inviteDialogVisible = ref(false)
const inviteForm = reactive({
  username: '',
  message: ''
})
const inviteFormRef = ref(null)

// 邀请表单验证规则
const inviteRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  message: [{ required: true, message: '请填写邀请信息', trigger: 'blur' }]
}

// 申请列表
const applicationList = ref([])

// 获取项目详情
const fetchProjectDetail = async () => {
  try {
    loading.value = true
    const res = await projectApi.getProjectDetail(projectId.value)
    projectDetail.value = res.data
  } catch (error) {
    console.error('获取项目详情失败:', error)
    ElMessage.error('获取项目详情失败')
    router.push('/dashboard/project')
  } finally {
    loading.value = false
  }
}

// 获取项目成员列表
const fetchMemberList = async () => {
  try {
    const res = await projectApi.getProjectMembers(projectId.value)
    memberList.value = res.data || []
  } catch (error) {
    console.error('获取项目成员列表失败:', error)
    ElMessage.error('获取项目成员列表失败')
  }
}

// 获取项目申请列表
const fetchApplicationList = async () => {
  try {
    const res = await projectApi.getProjectApplications(projectId.value)
    applicationList.value = res.data || []
  } catch (error) {
    console.error('获取项目申请列表失败:', error)
    ElMessage.error('获取项目申请列表失败')
  }
}

// 打开邀请成员对话框
const openInviteDialog = () => {
  inviteForm.username = ''
  inviteForm.message = ''
  inviteDialogVisible.value = true
}

// 提交邀请成员
const submitInviteMember = async () => {
  if (!inviteFormRef.value) return
  
  try {
    await inviteFormRef.value.validate()
    
    await projectApi.inviteMember(projectId.value, inviteForm)
    ElMessage.success('邀请已发送')
    inviteDialogVisible.value = false
  } catch (error) {
    console.error('邀请成员失败:', error)
    ElMessage.error('邀请成员失败')
  }
}

// 处理申请
const handleApplication = async (applicationId, status) => {
  try {
    await projectApi.handleProjectApplication(projectId.value, applicationId, { status })
    ElMessage.success(status === 'approved' ? '已批准申请' : '已拒绝申请')
    fetchApplicationList()
    if (status === 'approved') {
      fetchMemberList()
    }
  } catch (error) {
    console.error('处理申请失败:', error)
    ElMessage.error('处理申请失败')
  }
}

// 移除成员
const removeMember = async (memberId) => {
  try {
    await ElMessageBox.confirm('确定要移除该成员吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await projectApi.removeMember(projectId.value, memberId)
    ElMessage.success('成员已移除')
    fetchMemberList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('移除成员失败:', error)
      ElMessage.error('移除成员失败')
    }
  }
}

// 退出项目
const leaveProject = async () => {
  try {
    await ElMessageBox.confirm('确定要退出该项目吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await projectApi.leaveProject(projectId.value)
    ElMessage.success('已退出项目')
    router.push('/dashboard/project')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退出项目失败:', error)
      ElMessage.error('退出项目失败')
    }
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

// 获取成员角色显示文本
const getRoleText = (role) => {
  const roleMap = {
    owner: '创建者',
    member: '成员'
  }
  return roleMap[role] || '未知'
}

// 判断当前用户是否为项目创建者
const isProjectOwner = computed(() => {
  return projectDetail.value?.role === 'owner'
})

// 页面加载时获取数据
onMounted(async () => {
  await fetchProjectDetail()
  fetchMemberList()
  if (isProjectOwner.value) {
    fetchApplicationList()
  }
})
</script>

<template>
  <div class="project-detail-page" v-loading="loading">
    <div class="page-header">
      <div class="header-left">
        <el-button @click="router.push('/dashboard/project')" icon="ArrowLeft">返回</el-button>
        <h2 class="page-title">{{ projectDetail?.name }}</h2>
        <el-tag :type="getStatusType(projectDetail?.status)">
          {{ getStatusText(projectDetail?.status) }}
        </el-tag>
      </div>
      <div class="header-actions" v-if="projectDetail">
        <el-button 
          v-if="isProjectOwner && projectDetail.status === 'active'"
          type="primary"
          @click="openInviteDialog"
        >
          <el-icon><UserFilled /></el-icon> 邀请成员
        </el-button>
        <el-button 
          v-if="!isProjectOwner && projectDetail.status === 'active'"
          type="danger"
          @click="leaveProject"
        >
          <el-icon><CircleClose /></el-icon> 退出项目
        </el-button>
      </div>
    </div>
    
    <div v-if="projectDetail" class="project-content">
      <el-card class="project-info-card">
        <template #header>
          <div class="card-header">
            <h3>项目信息</h3>
          </div>
        </template>
        <div class="project-info">
          <div class="info-item">
            <span class="label">项目ID:</span>
            <span class="value">{{ projectDetail.id }}</span>
          </div>
          <div class="info-item">
            <span class="label">创建时间:</span>
            <span class="value">{{ projectDetail.createdAt }}</span>
          </div>
          <div class="info-item">
            <span class="label">开始日期:</span>
            <span class="value">{{ projectDetail.startDate }}</span>
          </div>
          <div class="info-item">
            <span class="label">结束日期:</span>
            <span class="value">{{ projectDetail.endDate }}</span>
          </div>
          <div class="info-item full-width">
            <span class="label">项目描述:</span>
            <div class="description-content">{{ projectDetail.description }}</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="member-card">
        <template #header>
          <div class="card-header">
            <h3>项目成员 ({{ memberList.length }})</h3>
          </div>
        </template>
        <el-empty v-if="memberList.length === 0" description="暂无成员" />
        <el-table v-else :data="memberList" style="width: 100%">
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="name" label="姓名" />
          <el-table-column label="角色" width="120">
            <template #default="scope">
              <el-tag :type="scope.row.role === 'owner' ? 'danger' : 'info'">
                {{ getRoleText(scope.row.role) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="joinTime" label="加入时间" width="180" />
          <el-table-column v-if="isProjectOwner" label="操作" width="120" fixed="right">
            <template #default="scope">
              <el-button 
                v-if="scope.row.role !== 'owner' && projectDetail.status === 'active'"
                type="danger" 
                size="small"
                @click="removeMember(scope.row.id)"
              >
                移除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
      
      <el-card v-if="isProjectOwner && projectDetail.status === 'active'" class="application-card">
        <template #header>
          <div class="card-header">
            <h3>加入申请 ({{ applicationList.length }})</h3>
          </div>
        </template>
        <el-empty v-if="applicationList.length === 0" description="暂无申请" />
        <el-table v-else :data="applicationList" style="width: 100%">
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="name" label="姓名" />
          <el-table-column prop="reason" label="申请理由" show-overflow-tooltip />
          <el-table-column prop="applyTime" label="申请时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button 
                type="success" 
                size="small"
                @click="handleApplication(scope.row.id, 'approved')"
              >
                批准
              </el-button>
              <el-button 
                type="danger" 
                size="small"
                @click="handleApplication(scope.row.id, 'rejected')"
              >
                拒绝
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
    
    <!-- 邀请成员对话框 -->
    <el-dialog
      v-model="inviteDialogVisible"
      title="邀请成员"
      width="500px"
    >
      <el-form
        ref="inviteFormRef"
        :model="inviteForm"
        :rules="inviteRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="inviteForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邀请信息" prop="message">
          <el-input
            v-model="inviteForm.message"
            type="textarea"
            :rows="3"
            placeholder="请填写邀请信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="inviteDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitInviteMember">发送邀请</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.project-detail-page {
  padding-bottom: 30px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.page-title {
  font-size: 24px;
  color: #333;
  margin: 0;
}

.project-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.project-info {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.info-item {
  width: calc(50% - 15px);
  display: flex;
  flex-direction: column;
}

.info-item.full-width {
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>