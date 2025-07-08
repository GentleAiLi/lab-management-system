<script setup>
import { ref, reactive, onMounted } from 'vue'
import { adminApi } from '../../api'

// 用户列表
const userList = ref([])

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

// 添加用户对话框
const addDialogVisible = ref(false)
const userForm = reactive({
  username: '',
  password: '',
  name: '',
  email: '',
  phone: ''
})
const userFormRef = ref(null)

// 用户表单验证规则
const userRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
}

// 获取用户列表
const fetchUserList = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.currentPage,
      limit: pagination.pageSize,
      keyword: searchKeyword.value
    }
    
    const res = await adminApi.getUserList(params)
    userList.value = res.data || []
    pagination.total = res.total || 0
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 处理页码变化
const handleCurrentChange = (page) => {
  pagination.currentPage = page
  fetchUserList()
}

// 处理每页条数变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  fetchUserList()
}

// 处理搜索
const handleSearch = () => {
  pagination.currentPage = 1
  fetchUserList()
}

// 打开添加用户对话框
const openAddDialog = () => {
  userForm.username = ''
  userForm.password = ''
  userForm.name = ''
  userForm.email = ''
  userForm.phone = ''
  addDialogVisible.value = true
}

// 提交添加用户
const submitAddUser = async () => {
  if (!userFormRef.value) return
  
  try {
    await userFormRef.value.validate()
    
    await adminApi.addUser(userForm)
    ElMessage.success('用户添加成功')
    addDialogVisible.value = false
    fetchUserList()
  } catch (error) {
    console.error('添加用户失败:', error)
    ElMessage.error('添加用户失败')
  }
}

// 处理用户注册申请
const handleUserRegistration = async (userId, status) => {
  try {
    await adminApi.handleUserRegistration(userId, { status })
    ElMessage.success(status === 'approved' ? '已批准注册申请' : '已拒绝注册申请')
    fetchUserList()
  } catch (error) {
    console.error('处理用户注册申请失败:', error)
    ElMessage.error('处理用户注册申请失败')
  }
}

// 禁用/启用用户
const toggleUserStatus = async (user) => {
  const newStatus = user.status === 'active' ? 'disabled' : 'active'
  const actionText = newStatus === 'active' ? '启用' : '禁用'
  
  try {
    await ElMessageBox.confirm(`确定要${actionText}该用户吗?`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await adminApi.updateUserStatus(user.id, { status: newStatus })
    ElMessage.success(`用户已${actionText}`)
    fetchUserList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(`${actionText}用户失败:`, error)
      ElMessage.error(`${actionText}用户失败`)
    }
  }
}

// 设置为管理员
const setAsAdmin = async (user) => {
  try {
    await ElMessageBox.confirm('确定要将该用户设置为管理员吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await adminApi.setUserAsAdmin(user.id)
    ElMessage.success('已设置为管理员')
    fetchUserList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('设置管理员失败:', error)
      ElMessage.error('设置管理员失败')
    }
  }
}

// 移除管理员权限
const removeAdminRole = async (user) => {
  try {
    await ElMessageBox.confirm('确定要移除该用户的管理员权限吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await adminApi.removeUserAdminRole(user.id)
    ElMessage.success('已移除管理员权限')
    fetchUserList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('移除管理员权限失败:', error)
      ElMessage.error('移除管理员权限失败')
    }
  }
}

// 获取用户状态标签类型
const getStatusType = (status) => {
  const statusMap = {
    pending: 'info',
    active: 'success',
    disabled: 'danger',
    rejected: 'warning'
  }
  return statusMap[status] || 'info'
}

// 获取用户状态显示文本
const getStatusText = (status) => {
  const statusMap = {
    pending: '待审核',
    active: '正常',
    disabled: '已禁用',
    rejected: '已拒绝'
  }
  return statusMap[status] || '未知'
}

// 页面加载时获取数据
onMounted(() => {
  fetchUserList()
})
</script>

<template>
  <div class="user-page">
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
      <el-button type="primary" @click="openAddDialog">
        <el-icon><Plus /></el-icon> 添加用户
      </el-button>
    </div>
    
    <div class="search-box">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索用户名/姓名/邮箱"
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
    
    <el-card v-loading="loading">
      <el-empty v-if="userList.length === 0" description="暂无用户数据" />
      <el-table v-else :data="userList" style="width: 100%">
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="name" label="姓名" min-width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column label="角色" width="100">
          <template #default="scope">
            <el-tag type="danger" v-if="scope.row.isAdmin">管理员</el-tag>
            <el-tag v-else>普通用户</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <!-- 待审核用户的操作 -->
            <template v-if="scope.row.status === 'pending'">
              <el-button 
                type="success" 
                size="small"
                @click="handleUserRegistration(scope.row.id, 'approved')"
              >
                批准
              </el-button>
              <el-button 
                type="danger" 
                size="small"
                @click="handleUserRegistration(scope.row.id, 'rejected')"
              >
                拒绝
              </el-button>
            </template>
            
            <!-- 正常/禁用用户的操作 -->
            <template v-else-if="scope.row.status === 'active' || scope.row.status === 'disabled'">
              <el-button 
                :type="scope.row.status === 'active' ? 'danger' : 'success'" 
                size="small"
                @click="toggleUserStatus(scope.row)"
              >
                {{ scope.row.status === 'active' ? '禁用' : '启用' }}
              </el-button>
              
              <!-- 管理员权限操作 -->
              <el-button 
                v-if="!scope.row.isAdmin"
                type="warning" 
                size="small"
                @click="setAsAdmin(scope.row)"
              >
                设为管理员
              </el-button>
              <el-button 
                v-else
                type="info" 
                size="small"
                @click="removeAdminRole(scope.row)"
              >
                移除管理员
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
    
    <!-- 添加用户对话框 -->
    <el-dialog
      v-model="addDialogVisible"
      title="添加用户"
      width="500px"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="userForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAddUser">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.user-page {
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