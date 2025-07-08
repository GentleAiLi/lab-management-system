<script setup>
import { ref, reactive, onMounted } from 'vue'
import { adminApi } from '../../api'

// 设备列表
const equipmentList = ref([])

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

// 添加设备对话框
const addDialogVisible = ref(false)
const equipmentForm = reactive({
  name: '',
  category: '',
  location: '',
  description: '',
  status: 'available'
})
const equipmentFormRef = ref(null)

// 设备表单验证规则
const equipmentRules = {
  name: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  category: [{ required: true, message: '请输入设备类别', trigger: 'blur' }],
  location: [{ required: true, message: '请输入设备位置', trigger: 'blur' }],
  description: [{ required: true, message: '请输入设备描述', trigger: 'blur' }],
  status: [{ required: true, message: '请选择设备状态', trigger: 'change' }]
}

// 设备详情对话框
const detailDialogVisible = ref(false)
const currentEquipment = ref(null)

// 借用申请列表
const borrowRequestList = ref([])

// 获取设备列表
const fetchEquipmentList = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.currentPage,
      limit: pagination.pageSize,
      keyword: searchKeyword.value,
      status: filterStatus.value !== 'all' ? filterStatus.value : undefined
    }
    
    const res = await adminApi.getEquipmentList(params)
    equipmentList.value = res.data || []
    pagination.total = res.total || 0
  } catch (error) {
    console.error('获取设备列表失败:', error)
    ElMessage.error('获取设备列表失败')
  } finally {
    loading.value = false
  }
}

// 获取借用申请列表
const fetchBorrowRequestList = async () => {
  try {
    const res = await adminApi.getBorrowRequestList()
    borrowRequestList.value = res.data || []
  } catch (error) {
    console.error('获取借用申请列表失败:', error)
    ElMessage.error('获取借用申请列表失败')
  }
}

// 处理页码变化
const handleCurrentChange = (page) => {
  pagination.currentPage = page
  fetchEquipmentList()
}

// 处理每页条数变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  fetchEquipmentList()
}

// 处理搜索
const handleSearch = () => {
  pagination.currentPage = 1
  fetchEquipmentList()
}

// 处理状态筛选
const handleStatusChange = () => {
  pagination.currentPage = 1
  fetchEquipmentList()
}

// 打开添加设备对话框
const openAddDialog = () => {
  equipmentForm.name = ''
  equipmentForm.category = ''
  equipmentForm.location = ''
  equipmentForm.description = ''
  equipmentForm.status = 'available'
  addDialogVisible.value = true
}

// 提交添加设备
const submitAddEquipment = async () => {
  if (!equipmentFormRef.value) return
  
  try {
    await equipmentFormRef.value.validate()
    
    await adminApi.addEquipment(equipmentForm)
    ElMessage.success('设备添加成功')
    addDialogVisible.value = false
    fetchEquipmentList()
  } catch (error) {
    console.error('添加设备失败:', error)
    ElMessage.error('添加设备失败')
  }
}

// 查看设备详情
const viewEquipmentDetail = (equipment) => {
  currentEquipment.value = equipment
  detailDialogVisible.value = true
}

// 处理借用申请
const handleBorrowRequest = async (requestId, status) => {
  try {
    await adminApi.handleBorrowRequest(requestId, { status })
    ElMessage.success(status === 'approved' ? '已批准借用申请' : '已拒绝借用申请')
    fetchBorrowRequestList()
    fetchEquipmentList()
  } catch (error) {
    console.error('处理借用申请失败:', error)
    ElMessage.error('处理借用申请失败')
  }
}

// 处理归还申请
const handleReturnRequest = async (requestId) => {
  try {
    await adminApi.handleReturnRequest(requestId, { status: 'approved' })
    ElMessage.success('已确认归还')
    fetchBorrowRequestList()
    fetchEquipmentList()
  } catch (error) {
    console.error('处理归还申请失败:', error)
    ElMessage.error('处理归还申请失败')
  }
}

// 获取设备状态标签类型
const getStatusType = (status) => {
  const statusMap = {
    available: 'success',
    borrowed: 'warning',
    pending: 'info',
    maintenance: 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取设备状态显示文本
const getStatusText = (status) => {
  const statusMap = {
    available: '可借用',
    borrowed: '已借出',
    pending: '审核中',
    maintenance: '维护中'
  }
  return statusMap[status] || '未知'
}

// 获取申请状态标签类型
const getRequestStatusType = (status) => {
  const statusMap = {
    pending: 'info',
    approved: 'success',
    rejected: 'danger',
    returned: 'warning'
  }
  return statusMap[status] || 'info'
}

// 获取申请状态显示文本
const getRequestStatusText = (status) => {
  const statusMap = {
    pending: '待审核',
    approved: '已批准',
    rejected: '已拒绝',
    returned: '已归还'
  }
  return statusMap[status] || '未知'
}

// 页面加载时获取数据
onMounted(() => {
  fetchEquipmentList()
  fetchBorrowRequestList()
})
</script>

<template>
  <div class="equipment-page">
    <div class="page-header">
      <h2 class="page-title">设备管理</h2>
      <el-button type="primary" @click="openAddDialog">
        <el-icon><Plus /></el-icon> 添加设备
      </el-button>
    </div>
    
    <div class="filter-container">
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索设备名称"
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
        <el-select v-model="filterStatus" placeholder="设备状态" @change="handleStatusChange">
          <el-option label="全部" value="all" />
          <el-option label="可借用" value="available" />
          <el-option label="已借出" value="borrowed" />
          <el-option label="审核中" value="pending" />
          <el-option label="维护中" value="maintenance" />
        </el-select>
      </div>
    </div>
    
    <el-tabs type="border-card" class="equipment-tabs">
      <el-tab-pane label="设备列表">
        <div v-loading="loading">
          <el-empty v-if="equipmentList.length === 0" description="暂无设备数据" />
          <el-table v-else :data="equipmentList" style="width: 100%">
            <el-table-column prop="name" label="设备名称" min-width="150" />
            <el-table-column prop="category" label="类别" width="120" />
            <el-table-column prop="location" label="位置" width="120" />
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="borrower" label="借用人" width="120" />
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="scope">
                <el-button 
                  type="primary" 
                  size="small"
                  @click="viewEquipmentDetail(scope.row)"
                >
                  详情
                </el-button>
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
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="借用申请">
        <el-empty v-if="borrowRequestList.length === 0" description="暂无借用申请" />
        <el-table v-else :data="borrowRequestList" style="width: 100%">
          <el-table-column prop="equipmentName" label="设备名称" min-width="150" />
          <el-table-column prop="borrower" label="借用人" width="120" />
          <el-table-column label="借用期限" width="240">
            <template #default="scope">
              {{ scope.row.startDate }} ~ {{ scope.row.endDate }}
            </template>
          </el-table-column>
          <el-table-column prop="purpose" label="借用目的" min-width="200" show-overflow-tooltip />
          <el-table-column label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getRequestStatusType(scope.row.status)">
                {{ getRequestStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="requestTime" label="申请时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <template v-if="scope.row.status === 'pending'">
                <el-button 
                  type="success" 
                  size="small"
                  @click="handleBorrowRequest(scope.row.id, 'approved')"
                >
                  批准
                </el-button>
                <el-button 
                  type="danger" 
                  size="small"
                  @click="handleBorrowRequest(scope.row.id, 'rejected')"
                >
                  拒绝
                </el-button>
              </template>
              <el-button 
                v-if="scope.row.status === 'returned'"
                type="primary" 
                size="small"
                @click="handleReturnRequest(scope.row.id)"
              >
                确认归还
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
    
    <!-- 添加设备对话框 -->
    <el-dialog
      v-model="addDialogVisible"
      title="添加设备"
      width="500px"
    >
      <el-form
        ref="equipmentFormRef"
        :model="equipmentForm"
        :rules="equipmentRules"
        label-width="100px"
      >
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="equipmentForm.name" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="设备类别" prop="category">
          <el-input v-model="equipmentForm.category" placeholder="请输入设备类别" />
        </el-form-item>
        <el-form-item label="设备位置" prop="location">
          <el-input v-model="equipmentForm.location" placeholder="请输入设备位置" />
        </el-form-item>
        <el-form-item label="设备描述" prop="description">
          <el-input
            v-model="equipmentForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入设备描述"
          />
        </el-form-item>
        <el-form-item label="设备状态" prop="status">
          <el-select v-model="equipmentForm.status" placeholder="请选择设备状态" style="width: 100%">
            <el-option label="可借用" value="available" />
            <el-option label="维护中" value="maintenance" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAddEquipment">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 设备详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="设备详情"
      width="600px"
    >
      <div v-if="currentEquipment" class="equipment-detail">
        <div class="detail-item">
          <span class="label">设备名称:</span>
          <span class="value">{{ currentEquipment.name }}</span>
        </div>
        <div class="detail-item">
          <span class="label">设备类别:</span>
          <span class="value">{{ currentEquipment.category }}</span>
        </div>
        <div class="detail-item">
          <span class="label">设备位置:</span>
          <span class="value">{{ currentEquipment.location }}</span>
        </div>
        <div class="detail-item">
          <span class="label">设备状态:</span>
          <span class="value">
            <el-tag :type="getStatusType(currentEquipment.status)">
              {{ getStatusText(currentEquipment.status) }}
            </el-tag>
          </span>
        </div>
        <div class="detail-item" v-if="currentEquipment.borrower">
          <span class="label">借用人:</span>
          <span class="value">{{ currentEquipment.borrower }}</span>
        </div>
        <div class="detail-item" v-if="currentEquipment.borrowDate">
          <span class="label">借用日期:</span>
          <span class="value">{{ currentEquipment.borrowDate }}</span>
        </div>
        <div class="detail-item" v-if="currentEquipment.returnDate">
          <span class="label">预计归还:</span>
          <span class="value">{{ currentEquipment.returnDate }}</span>
        </div>
        <div class="detail-item full-width">
          <span class="label">设备描述:</span>
          <div class="description-content">{{ currentEquipment.description }}</div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.equipment-page {
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

.equipment-tabs {
  margin-top: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.equipment-detail {
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>