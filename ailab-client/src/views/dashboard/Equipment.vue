<script setup>
import { ref, reactive, onMounted } from 'vue'
import { equipmentApi } from '../../api'

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

// 借用设备对话框
const borrowDialogVisible = ref(false)
const currentEquipment = ref(null)
const borrowForm = reactive({
  startDate: '',
  endDate: '',
  purpose: ''
})
const borrowFormRef = ref(null)

// 借用表单验证规则
const borrowRules = {
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
  purpose: [{ required: true, message: '请填写借用目的', trigger: 'blur' }]
}

// 获取设备列表
const fetchEquipmentList = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.currentPage,
      limit: pagination.pageSize,
      keyword: searchKeyword.value
    }
    
    const res = await equipmentApi.getEquipmentList(params)
    equipmentList.value = res.data || []
    pagination.total = res.total || 0
  } catch (error) {
    console.error('获取设备列表失败:', error)
    ElMessage.error('获取设备列表失败')
  } finally {
    loading.value = false
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

// 打开借用对话框
const openBorrowDialog = (equipment) => {
  currentEquipment.value = equipment
  borrowForm.startDate = ''
  borrowForm.endDate = ''
  borrowForm.purpose = ''
  borrowDialogVisible.value = true
}

// 提交借用申请
const submitBorrowRequest = async () => {
  if (!borrowFormRef.value) return
  
  try {
    await borrowFormRef.value.validate()
    
    const borrowData = {
      startDate: borrowForm.startDate,
      endDate: borrowForm.endDate,
      purpose: borrowForm.purpose
    }
    
    await equipmentApi.borrowEquipment(currentEquipment.value.id, borrowData)
    ElMessage.success('借用申请已提交，请等待审核')
    borrowDialogVisible.value = false
    fetchEquipmentList()
  } catch (error) {
    console.error('提交借用申请失败:', error)
    ElMessage.error('提交借用申请失败')
  }
}

// 归还设备
const returnEquipment = async (equipment) => {
  try {
    await ElMessageBox.confirm('确定要归还该设备吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await equipmentApi.returnEquipment(equipment.id)
    ElMessage.success('设备归还申请已提交')
    fetchEquipmentList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('归还设备失败:', error)
      ElMessage.error('归还设备失败')
    }
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

// 页面加载时获取数据
onMounted(() => {
  fetchEquipmentList()
})
</script>

<template>
  <div class="equipment-page">
    <div class="page-header">
      <h2 class="page-title">设备管理</h2>
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
    </div>
    
    <el-card v-loading="loading">
      <div class="equipment-list">
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
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button 
                v-if="scope.row.status === 'available'"
                type="primary" 
                size="small"
                @click="openBorrowDialog(scope.row)"
              >
                借用
              </el-button>
              <el-button 
                v-if="scope.row.status === 'borrowed' && scope.row.borrower === 'current_user'"
                type="warning" 
                size="small"
                @click="returnEquipment(scope.row)"
              >
                归还
              </el-button>
              <el-button 
                type="info" 
                size="small"
                @click="$router.push(`/equipment/${scope.row.id}`)"
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
    </el-card>
    
    <!-- 借用设备对话框 -->
    <el-dialog
      v-model="borrowDialogVisible"
      title="借用设备"
      width="500px"
    >
      <el-form
        ref="borrowFormRef"
        :model="borrowForm"
        :rules="borrowRules"
        label-width="100px"
      >
        <el-form-item label="设备名称">
          <span>{{ currentEquipment?.name }}</span>
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker
            v-model="borrowForm.startDate"
            type="date"
            placeholder="选择开始日期"
            style="width: 100%"
            :disabled-date="date => date < new Date()"
          />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker
            v-model="borrowForm.endDate"
            type="date"
            placeholder="选择结束日期"
            style="width: 100%"
            :disabled-date="date => !borrowForm.startDate || date < borrowForm.startDate"
          />
        </el-form-item>
        <el-form-item label="借用目的" prop="purpose">
          <el-input
            v-model="borrowForm.purpose"
            type="textarea"
            :rows="3"
            placeholder="请简要说明借用目的"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="borrowDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitBorrowRequest">提交申请</el-button>
        </span>
      </template>
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

.search-box {
  width: 300px;
}

.equipment-list {
  margin-top: 20px;
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