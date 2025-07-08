<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { articleApi } from '../../api'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()

// 文章ID（编辑模式）
const articleId = computed(() => route.params.id)

// 判断是否为编辑模式
const isEditMode = computed(() => !!articleId.value)

// 文章表单
const articleForm = reactive({
  title: '',
  content: '',
  tags: []
})

// 标签输入
const tagInputVisible = ref(false)
const tagInputValue = ref('')
const tagInputRef = ref(null)

// 加载状态
const loading = ref(false)
const submitting = ref(false)

// 编辑器配置
const editorConfig = {
  toolbars: [
    'bold', 'italic', 'strikethrough', 'title', 'sub', 'sup', 'quote', 'unordered-list',
    'ordered-list', 'task-list', 'code', 'code-block', 'table', 'link', 'image',
    'clear', 'preview', 'fullscreen'
  ],
  height: 500
}

// 获取文章详情（编辑模式）
const fetchArticleDetail = async () => {
  if (!isEditMode.value) return
  
  try {
    loading.value = true
    const res = await articleApi.getArticleDetail(articleId.value)
    const article = res.data
    
    if (!article) {
      ElMessage.error('文章不存在')
      router.push('/dashboard/article')
      return
    }
    
    // 只有草稿或被拒绝的文章可以编辑
    if (article.status !== 'draft' && article.status !== 'rejected') {
      ElMessage.warning('只有草稿或被拒绝的文章可以编辑')
      router.push('/dashboard/article')
      return
    }
    
    articleForm.title = article.title || ''
    articleForm.content = article.content || ''
    articleForm.tags = article.tags || []
  } catch (error) {
    console.error('获取文章详情失败:', error)
    ElMessage.error('获取文章详情失败')
    router.push('/dashboard/article')
  } finally {
    loading.value = false
  }
}

// 显示标签输入框
const showTagInput = () => {
  tagInputVisible.value = true
  // 在下一个DOM更新周期后聚焦输入框
  setTimeout(() => {
    tagInputRef.value?.input?.focus()
  })
}

// 处理标签输入确认
const handleTagInputConfirm = () => {
  if (tagInputValue.value) {
    if (articleForm.tags.includes(tagInputValue.value)) {
      ElMessage.warning('标签已存在')
    } else if (articleForm.tags.length >= 5) {
      ElMessage.warning('最多添加5个标签')
    } else {
      articleForm.tags.push(tagInputValue.value)
    }
  }
  tagInputVisible.value = false
  tagInputValue.value = ''
}

// 移除标签
const removeTag = (tag) => {
  articleForm.tags = articleForm.tags.filter(t => t !== tag)
}

// 保存为草稿
const saveDraft = async () => {
  if (!articleForm.title.trim()) {
    ElMessage.warning('请输入文章标题')
    return
  }
  
  if (!articleForm.content.trim()) {
    ElMessage.warning('请输入文章内容')
    return
  }
  
  try {
    submitting.value = true
    
    if (isEditMode.value) {
      await articleApi.updateArticle(articleId.value, { ...articleForm, status: 'draft' })
      ElMessage.success('草稿已更新')
    } else {
      await articleApi.createArticle({ ...articleForm, status: 'draft' })
      ElMessage.success('草稿已保存')
      router.push('/dashboard/article')
    }
  } catch (error) {
    console.error('保存草稿失败:', error)
    ElMessage.error('保存草稿失败')
  } finally {
    submitting.value = false
  }
}

// 提交审核
const submitForReview = async () => {
  if (!articleForm.title.trim()) {
    ElMessage.warning('请输入文章标题')
    return
  }
  
  if (!articleForm.content.trim()) {
    ElMessage.warning('请输入文章内容')
    return
  }
  
  if (articleForm.content.length < 50) {
    ElMessage.warning('文章内容太短，请至少输入50个字符')
    return
  }
  
  try {
    submitting.value = true
    
    if (isEditMode.value) {
      await articleApi.updateArticle(articleId.value, { ...articleForm, status: 'pending' })
      ElMessage.success('文章已提交审核')
    } else {
      await articleApi.createArticle({ ...articleForm, status: 'pending' })
      ElMessage.success('文章已提交审核')
    }
    
    router.push('/dashboard/article')
  } catch (error) {
    console.error('提交审核失败:', error)
    ElMessage.error('提交审核失败')
  } finally {
    submitting.value = false
  }
}

// 取消编辑
const cancelEdit = () => {
  ElMessageBox.confirm('确定要取消编辑吗？未保存的内容将丢失', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    router.push('/dashboard/article')
  }).catch(() => {})
}

// 页面加载时获取数据
onMounted(() => {
  fetchArticleDetail()
})
</script>

<template>
  <div class="article-edit-page" v-loading="loading">
    <div class="page-header">
      <div class="header-left">
        <el-button @click="cancelEdit" icon="ArrowLeft">返回</el-button>
        <h2 class="page-title">{{ isEditMode ? '编辑文章' : '写文章' }}</h2>
      </div>
      <div class="header-actions">
        <el-button 
          :loading="submitting" 
          @click="saveDraft"
        >
          保存草稿
        </el-button>
        <el-button 
          type="primary" 
          :loading="submitting" 
          @click="submitForReview"
        >
          提交审核
        </el-button>
      </div>
    </div>
    
    <el-card class="edit-card">
      <div class="title-input">
        <el-input
          v-model="articleForm.title"
          placeholder="请输入文章标题"
          maxlength="100"
          show-word-limit
        />
      </div>
      
      <div class="tags-container">
        <el-tag
          v-for="tag in articleForm.tags"
          :key="tag"
          closable
          @close="removeTag(tag)"
          class="article-tag"
        >
          {{ tag }}
        </el-tag>
        <el-input
          v-if="tagInputVisible"
          ref="tagInputRef"
          v-model="tagInputValue"
          class="tag-input"
          size="small"
          @keyup.enter="handleTagInputConfirm"
          @blur="handleTagInputConfirm"
        />
        <el-button 
          v-else 
          class="button-new-tag" 
          size="small" 
          @click="showTagInput"
          :disabled="articleForm.tags.length >= 5"
        >
          + 添加标签
        </el-button>
      </div>
      
      <div class="editor-container">
        <md-editor 
          v-model="articleForm.content" 
          :toolbars="editorConfig.toolbars"
          :height="editorConfig.height"
          preview-theme="github"
          code-theme="atom-one-dark"
        />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.article-edit-page {
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

.edit-card {
  margin-bottom: 20px;
}

.title-input {
  margin-bottom: 20px;
}

.tags-container {
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.article-tag {
  margin-right: 0;
}

.tag-input {
  width: 100px;
  margin-right: 0;
}

.editor-container {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}
</style>