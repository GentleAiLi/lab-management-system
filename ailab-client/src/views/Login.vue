<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { userApi } from '../api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 登录/注册表单
const formRef = ref(null)
const formData = reactive({
  accountName: '',
  password: '',
  confirmPassword: '',
  email: '',
  name: ''
})

// 表单验证规则
const rules = {
  accountName: [
    { required: true, message: '请输入账户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== formData.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ]
}

// 是否为注册模式
const isRegisterMode = ref(false)

// 加载状态
const loading = ref(false)

// 切换登录/注册模式
const toggleMode = () => {
  isRegisterMode.value = !isRegisterMode.value
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    if (isRegisterMode.value) {
      // 注册
      await userApi.register({
        accountName: formData.accountName,
        password: formData.password,
        email: formData.email,
        name: formData.name
      })
      
      ElMessage.success('注册申请已提交，请等待管理员审核')
      isRegisterMode.value = false
    } else {
      // 登录
      const res = await userApi.login({
        accountName: formData.accountName,
        password: formData.password
      })
      
      if (res.code === 1 && res.data) {
        // 保存用户信息和token
        const userData = res.data
        const userInfo = {
          id: userData.id,
          username: userData.accountName,
          role: userData.role === 1 ? 'admin' : 'user'
        }
        
        userStore.setToken(userData.accessToken)
        userStore.setUserInfo(userInfo)
        
        ElMessage.success('登录成功')
        
        // 跳转到之前的页面或默认页面
        const redirectPath = route.query.redirect || '/dashboard'
        router.push(redirectPath)
      } else {
        ElMessage.error(res.message || '登录失败，请检查账户名和密码')
      }
    }
  } catch (error) {
    console.error('表单提交错误:', error)
    ElMessage.error(error.response?.data?.message || '操作失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2 class="login-title">{{ isRegisterMode ? '用户注册' : '用户登录' }}</h2>
        <p class="login-subtitle">AI实验室管理系统</p>
      </div>
      
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-position="top"
        class="login-form"
      >
        <el-form-item label="账户名" prop="accountName">
          <el-input 
            v-model="formData.accountName" 
            placeholder="请输入账户名"
            prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="formData.password" 
            type="password" 
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <template v-if="isRegisterMode">
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input 
              v-model="formData.confirmPassword" 
              type="password" 
              placeholder="请再次输入密码"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          
          <el-form-item label="邮箱" prop="email">
            <el-input 
              v-model="formData.email" 
              placeholder="请输入邮箱"
              prefix-icon="Message"
            />
          </el-form-item>
          
          <el-form-item label="姓名" prop="name">
            <el-input 
              v-model="formData.name" 
              placeholder="请输入姓名"
              prefix-icon="User"
            />
          </el-form-item>
        </template>
        
        <el-form-item>
          <el-button 
            type="primary" 
            class="submit-btn" 
            :loading="loading"
            @click="submitForm"
          >
            {{ isRegisterMode ? '注册' : '登录' }}
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-footer">
        <el-button link @click="toggleMode">
          {{ isRegisterMode ? '已有账号？返回登录' : '没有账号？立即注册' }}
        </el-button>
        <el-button link @click="router.push('/')">
          返回首页
        </el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8f0 100%);
}

.login-box {
  width: 400px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-title {
  font-size: 24px;
  color: #333;
  margin-bottom: 10px;
}

.login-subtitle {
  color: #999;
  font-size: 14px;
}

.login-form {
  margin-bottom: 20px;
}

.submit-btn {
  width: 100%;
  padding: 12px 0;
  font-size: 16px;
}

.login-footer {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}
</style>