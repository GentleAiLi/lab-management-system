<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { articleApi } from '../../api'
import MdEditor from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'

const route = useRoute()
const router = useRouter()

// 文章ID
const articleId = computed(() => route.params.id)

// 文章详情
const article = ref(null)

// 加载状态
const loading = ref(false)

// 获取文章详情
const fetchArticleDetail = async () => {
  try {
    loading.value = true
    const res = await articleApi.getArticleDetail(articleId.value)
    article.value = res.data
    
    if (!article.value) {
      ElMessage.error('文章不存在')
      router.push('/dashboard/article')
    }
  } catch (error) {
    console.error('获取文章详情失败:', error)
    ElMessage.error('获取文章详情失败')
    router.push('/dashboard/article')
  } finally {
    loading.value = false
  }
}

// 编辑文章
const editArticle = () => {
  router.push(`/dashboard/article/edit/${articleId.value}`)
}

// 删除文章
const deleteArticle = async () => {
  try {
    await ElMessageBox.confirm('确定要删除该文章吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await articleApi.deleteArticle(articleId.value)
    ElMessage.success('文章已删除')
    router.push('/dashboard/article')
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

// 页面加载时获取数据
onMounted(() => {
  fetchArticleDetail()
})
</script>

<template>
  <div class="article-detail-page" v-loading="loading">
    <div class="page-header">
      <div class="header-left">
        <el-button @click="router.push('/dashboard/article')" icon="ArrowLeft">返回</el-button>
      </div>
      <div class="header-actions" v-if="article">
        <el-tag :type="getStatusType(article.status)" class="status-tag">
          {{ getStatusText(article.status) }}
        </el-tag>
        <el-button 
          v-if="article.status === 'draft' || article.status === 'rejected'"
          type="primary" 
          @click="editArticle"
        >
          编辑
        </el-button>
        <el-button 
          type="danger" 
          @click="deleteArticle"
        >
          删除
        </el-button>
      </div>
    </div>
    
    <div v-if="article" class="article-content">
      <div class="article-header">
        <h1 class="article-title">{{ article.title }}</h1>
        <div class="article-meta">
          <span class="meta-item">
            <el-icon><Calendar /></el-icon>
            {{ formatDate(article.createdAt) }}
          </span>
          <span class="meta-item" v-if="article.updatedAt && article.updatedAt !== article.createdAt">
            <el-icon><Edit /></el-icon>
            更新于 {{ formatDate(article.updatedAt) }}
          </span>
        </div>
        <div class="article-tags" v-if="article.tags && article.tags.length > 0">
          <el-tag
            v-for="tag in article.tags"
            :key="tag"
            class="article-tag"
            effect="plain"
          >
            {{ tag }}
          </el-tag>
        </div>
      </div>
      
      <el-divider />
      
      <div class="markdown-content">
        <md-editor 
          v-model="article.content" 
          preview-only 
          preview-theme="github"
          code-theme="atom-one-dark"
        />
      </div>
      
      <div class="article-footer" v-if="article.status === 'rejected'">
        <el-alert
          title="审核未通过"
          type="error"
          :description="article.rejectReason || '无拒绝原因'"
          show-icon
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.article-detail-page {
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

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.status-tag {
  margin-right: 10px;
}

.article-content {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.article-header {
  margin-bottom: 20px;
}

.article-title {
  font-size: 28px;
  color: #333;
  margin: 0 0 15px 0;
}

.article-meta {
  display: flex;
  align-items: center;
  color: #999;
  font-size: 14px;
  margin-bottom: 15px;
}

.meta-item {
  display: flex;
  align-items: center;
  margin-right: 20px;
}

.meta-item .el-icon {
  margin-right: 5px;
}

.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.article-tag {
  margin-right: 0;
}

.markdown-content {
  margin-top: 20px;
}

.article-footer {
  margin-top: 30px;
}
</style>