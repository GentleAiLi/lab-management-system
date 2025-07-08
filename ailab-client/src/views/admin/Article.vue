<script setup>
import { ref, reactive, onMounted } from 'vue'
import { adminApi } from '../../api'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { ElMessage } from 'element-plus'


// 文章列表
const articleList = ref([])

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
const filterStatus = ref('pending')

// 文章详情对话框
const detailDialogVisible = ref(false)
const currentArticle = ref(null)

// 获取文章列表
const fetchArticleList = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.currentPage,
      limit: pagination.pageSize,
      keyword: searchKeyword.value,
      status: filterStatus.value !== 'all' ? filterStatus.value : undefined
    }
    
    const res = await adminApi.getArticleList(params)
    articleList.value = res.data || []
    pagination.total = res.total || 0
  } catch (error) {
    console.error('获取文章列表失败:', error)
    ElMessage.error('获取文章列表失败')
  } finally {
    loading.value = false
  }
}

// 处理页码变化
const handleCurrentChange = (page) => {
  pagination.currentPage = page
  fetchArticleList()
}

// 处理每页条数变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  fetchArticleList()
}

// 处理搜索
const handleSearch = () => {
  pagination.currentPage = 1
  fetchArticleList()
}

// 处理状态筛选
const handleStatusChange = () => {
  pagination.currentPage = 1
  fetchArticleList()
}

// 查看文章详情
const viewArticleDetail = async (article) => {
  try {
    const res = await adminApi.getArticleDetail(article.id)
    currentArticle.value = res.data
    detailDialogVisible.value = true
  } catch (error) {
    console.error('获取文章详情失败:', error)
    ElMessage.error('获取文章详情失败')
  }
}

// 处理文章审核
const handleArticleReview = async (articleId, status, reason = '') => {
  try {
    await adminApi.reviewArticle(articleId, { status, reason })
    ElMessage.success(status === 'published' ? '文章已发布' : '文章已拒绝')
    fetchArticleList()
    if (detailDialogVisible.value) {
      detailDialogVisible.value = false
    }
  } catch (error) {
    console.error('处理文章审核失败:', error)
    ElMessage.error('处理文章审核失败')
  }
}

// 拒绝文章（带原因）
const rejectArticleWithReason = async (articleId) => {
  try {
    const { value: reason } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝文章', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '请输入拒绝原因'
    })
    
    if (reason) {
      await handleArticleReview(articleId, 'rejected', reason)
    }
  } catch (error) {
    // 用户取消输入
  }
}

// 获取文章状态标签类型
const getStatusType = (status) => {
  const statusMap = {
    draft: '',
    pending: 'info',
    published: 'success',
    rejected: 'danger'
  }
  return statusMap[status] || ''
}

// 获取文章状态显示文本
const getStatusText = (status) => {
  const statusMap = {
    draft: '草稿',
    pending: '待审核',
    published: '已发布',
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
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 截取文章内容预览
const truncateContent = (content, length = 100) => {
  if (!content) return ''
  if (content.length <= length) return content
  return content.substring(0, length) + '...'
}

// 页面加载时获取数据
onMounted(() => {
  fetchArticleList()
})
</script>

<template>
  <div class="article-page">
    <div class="page-header">
      <h2 class="page-title">文章审核</h2>
    </div>
    
    <div class="filter-container">
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索文章标题"
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
        <el-select v-model="filterStatus" placeholder="文章状态" @change="handleStatusChange">
          <el-option label="全部" value="all" />
          <el-option label="待审核" value="pending" />
          <el-option label="已发布" value="published" />
          <el-option label="已拒绝" value="rejected" />
        </el-select>
      </div>
    </div>
    
    <el-card v-loading="loading">
      <el-empty v-if="articleList.length === 0" description="暂无文章数据" />
      <el-table v-else :data="articleList" style="width: 100%">
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column label="内容预览" min-width="300" show-overflow-tooltip>
          <template #default="scope">
            <span>{{ truncateContent(scope.row.content) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button 
              type="primary" 
              size="small"
              @click="viewArticleDetail(scope.row)"
            >
              查看
            </el-button>
            <template v-if="scope.row.status === 'pending'">
              <el-button 
                type="success" 
                size="small"
                @click="handleArticleReview(scope.row.id, 'published')"
              >
                发布
              </el-button>
              <el-button 
                type="danger" 
                size="small"
                @click="rejectArticleWithReason(scope.row.id)"
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
    
    <!-- 文章详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="文章详情"
      width="800px"
      class="article-detail-dialog"
    >
      <div v-if="currentArticle" class="article-detail">
        <div class="detail-header">
          <h3 class="detail-title">{{ currentArticle.title }}</h3>
          <div class="detail-meta">
            <span class="meta-item">
              <el-icon><User /></el-icon>
              {{ currentArticle.author }}
            </span>
            <span class="meta-item">
              <el-icon><Calendar /></el-icon>
              {{ formatDate(currentArticle.createdAt) }}
            </span>
            <el-tag :type="getStatusType(currentArticle.status)">
              {{ getStatusText(currentArticle.status) }}
            </el-tag>
          </div>
          <div class="article-tags" v-if="currentArticle.tags && currentArticle.tags.length > 0">
            <el-tag
              v-for="tag in currentArticle.tags"
              :key="tag"
              class="article-tag"
              effect="plain"
              size="small"
            >
              {{ tag }}
            </el-tag>
          </div>
        </div>
        
        <div class="markdown-content">
          <md-editor 
            v-model="currentArticle.content" 
            preview-only 
            preview-theme="github"
            code-theme="atom-one-dark"
          />
        </div>
        
        <div class="detail-actions" v-if="currentArticle.status === 'pending'">
          <el-button 
            type="success" 
            @click="handleArticleReview(currentArticle.id, 'published')"
          >
            发布文章
          </el-button>
          <el-button 
            type="danger" 
            @click="rejectArticleWithReason(currentArticle.id)"
          >
            拒绝文章
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.article-page {
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

.article-detail {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-header {
  margin-bottom: 20px;
}

.detail-title {
  font-size: 24px;
  color: #333;
  margin: 0 0 15px 0;
}

.detail-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  color: #666;
  margin-bottom: 15px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 10px;
}

.article-tag {
  margin-right: 0;
}

.markdown-content {
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.detail-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}

:deep(.article-detail-dialog .el-dialog__body) {
  padding-top: 10px;
}
</style>