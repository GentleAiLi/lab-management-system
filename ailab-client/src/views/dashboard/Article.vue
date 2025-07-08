<script setup>
import { ref, reactive, onMounted } from 'vue'
import { articleApi } from '../../api'
import { useRouter } from 'vue-router'

const router = useRouter()

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

// 获取文章列表
const fetchArticleList = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.currentPage,
      limit: pagination.pageSize,
      keyword: searchKeyword.value
    }
    
    const res = await articleApi.getArticleList(params)
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

// 创建新文章
const createArticle = () => {
  router.push('/dashboard/article/edit')
}

// 编辑文章
const editArticle = (articleId) => {
  router.push(`/dashboard/article/edit/${articleId}`)
}

// 查看文章
const viewArticle = (articleId) => {
  router.push(`/dashboard/article/${articleId}`)
}

// 删除文章
const deleteArticle = async (articleId) => {
  try {
    await ElMessageBox.confirm('确定要删除该文章吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await articleApi.deleteArticle(articleId)
    ElMessage.success('文章已删除')
    fetchArticleList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除文章失败:', error)
      ElMessage.error('删除文章失败')
    }
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
    pending: '审核中',
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
      <h2 class="page-title">文章管理</h2>
      <el-button type="primary" @click="createArticle">
        <el-icon><EditPen /></el-icon> 写文章
      </el-button>
    </div>
    
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
    
    <el-card v-loading="loading">
      <el-empty v-if="articleList.length === 0" description="暂无文章" />
      <el-table v-else :data="articleList" style="width: 100%">
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column label="内容预览" min-width="300" show-overflow-tooltip>
          <template #default="scope">
            <span>{{ truncateContent(scope.row.content) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button 
              type="primary" 
              size="small"
              @click="viewArticle(scope.row.id)"
            >
              查看
            </el-button>
            <el-button 
              v-if="scope.row.status === 'draft' || scope.row.status === 'rejected'"
              type="success" 
              size="small"
              @click="editArticle(scope.row.id)"
            >
              编辑
            </el-button>
            <el-button 
              type="danger" 
              size="small"
              @click="deleteArticle(scope.row.id)"
            >
              删除
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
    </el-card>
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

.search-box {
  margin-bottom: 20px;
  max-width: 400px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>