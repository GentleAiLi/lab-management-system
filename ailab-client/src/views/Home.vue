<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

// 导航菜单
const navItems = [
  { name: '首页', path: '/' },
  { name: '设备', path: '/#equipment' },
  { name: '项目', path: '/#projects' },
  { name: '文章', path: '/#articles' },
  { name: '关于', path: '/#about' }
]

// 设备列表
const equipmentList = ref([
  {
    id: 1,
    name: '高性能计算服务器',
    image: 'https://via.placeholder.com/300x200',
    description: '配备最新的GPU和大容量内存，适用于深度学习和大规模数据处理。'
  },
  {
    id: 2,
    name: '机器人实验平台',
    image: 'https://via.placeholder.com/300x200',
    description: '集成多种传感器和执行器的机器人平台，支持各类机器人算法研究。'
  },
  {
    id: 3,
    name: '虚拟现实设备',
    image: 'https://via.placeholder.com/300x200',
    description: '高端VR头显和交互设备，支持沉浸式虚拟现实应用开发。'
  },
  {
    id: 4,
    name: '无人机开发套件',
    image: 'https://via.placeholder.com/300x200',
    description: '包含多旋翼无人机和开发工具，适用于计算机视觉和自主导航研究。'
  }
])

// 文章列表
const articleList = ref([
  {
    id: 1,
    title: '人工智能在医疗领域的应用',
    author: '张教授',
    date: '2023-10-15',
    summary: '本文探讨了人工智能技术如何改变现代医疗诊断和治疗方法...'
  },
  {
    id: 2,
    title: '深度学习模型优化技术综述',
    author: '李研究员',
    date: '2023-09-28',
    summary: '随着深度学习模型规模不断扩大，如何有效优化模型性能成为关键问题...'
  },
  {
    id: 3,
    title: '机器人感知系统的最新进展',
    author: '王博士',
    date: '2023-08-17',
    summary: '本文介绍了机器人视觉、触觉和听觉感知系统的最新研究成果...'
  }
])

// 处理进入系统按钮点击
const handleEnterSystem = async () => {
  if (userStore.isLoggedIn) {
    // 已登录，直接进入系统
    router.push('/dashboard')
  } else {
    // 尝试刷新令牌
    try {
      const success = await userStore.refreshToken()
      if (success) {
        // 刷新成功，进入系统
        router.push('/dashboard')
      } else {
        // 刷新失败，跳转到登录页
        router.push('/login')
      }
    } catch (error) {
      console.error('刷新令牌失败:', error)
      router.push('/login')
    }
  }
}

// 滚动到指定部分
const scrollToSection = (sectionId) => {
  const element = document.getElementById(sectionId)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' })
  }
}

// 处理导航点击
const handleNavClick = (path) => {
  if (path === '/') {
    window.scrollTo({ top: 0, behavior: 'smooth' })
  } else if (path.startsWith('/#')) {
    const sectionId = path.substring(2)
    scrollToSection(sectionId)
  } else {
    router.push(path)
  }
}
</script>

<template>
  <div class="home-page">
    <!-- 导航栏 -->
    <header class="header">
      <div class="container header-container">
        <div class="logo">AI实验室管理系统</div>
        <nav class="nav">
          <ul class="nav-list">
            <li v-for="item in navItems" :key="item.name" class="nav-item">
              <a @click.prevent="handleNavClick(item.path)" href="#" class="nav-link">{{ item.name }}</a>
            </li>
          </ul>
        </nav>
        <div class="user-actions">
          <el-button type="primary" @click="handleEnterSystem">进入系统</el-button>
        </div>
      </div>
    </header>

    <!-- 主横幅 -->
    <section class="banner">
      <div class="container">
        <div class="banner-content">
          <h1 class="banner-title">欢迎来到AI实验室</h1>
          <p class="banner-description">我们致力于人工智能技术的研究与应用，为学生和研究人员提供先进的设备和资源支持</p>
          <el-button type="primary" size="large" @click="handleEnterSystem">立即加入</el-button>
        </div>
      </div>
    </section>

    <!-- 设备展示 -->
    <section id="equipment" class="section">
      <div class="container">
        <h2 class="section-title">实验室设备</h2>
        <p class="section-description">我们配备了先进的硬件设备，支持各类人工智能研究和应用开发</p>
        
        <div class="equipment-grid">
          <div v-for="equipment in equipmentList" :key="equipment.id" class="equipment-card">
            <div class="equipment-image">
              <img :src="equipment.image" :alt="equipment.name">
            </div>
            <div class="equipment-info">
              <h3 class="equipment-name">{{ equipment.name }}</h3>
              <p class="equipment-description">{{ equipment.description }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 项目介绍 -->
    <section id="projects" class="section section-alt">
      <div class="container">
        <h2 class="section-title">研究项目</h2>
        <p class="section-description">我们开展多个前沿研究项目，欢迎有兴趣的师生参与</p>
        
        <div class="projects-showcase">
          <div class="project-item">
            <el-icon class="project-icon"><Monitor /></el-icon>
            <h3 class="project-title">计算机视觉</h3>
            <p class="project-description">研究目标检测、图像分割、视频分析等计算机视觉技术及其应用</p>
          </div>
          <div class="project-item">
            <el-icon class="project-icon"><ChatDotRound /></el-icon>
            <h3 class="project-title">自然语言处理</h3>
            <p class="project-description">研究文本理解、机器翻译、对话系统等自然语言处理技术</p>
          </div>
          <div class="project-item">
            <el-icon class="project-icon"><DataAnalysis /></el-icon>
            <h3 class="project-title">数据挖掘</h3>
            <p class="project-description">研究大规模数据分析、知识图谱、推荐系统等数据挖掘技术</p>
          </div>
          <div class="project-item">
            <el-icon class="project-icon"><SetUp /></el-icon>
            <h3 class="project-title">机器人技术</h3>
            <p class="project-description">研究机器人感知、规划、控制等技术，开发智能机器人系统</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 文章列表 -->
    <section id="articles" class="section">
      <div class="container">
        <h2 class="section-title">最新文章</h2>
        <p class="section-description">了解实验室的最新研究成果和技术动态</p>
        
        <div class="article-list">
          <div v-for="article in articleList" :key="article.id" class="article-card">
            <h3 class="article-title">{{ article.title }}</h3>
            <div class="article-meta">
              <span class="article-author">{{ article.author }}</span>
              <span class="article-date">{{ article.date }}</span>
            </div>
            <p class="article-summary">{{ article.summary }}</p>
            <el-button link>阅读更多</el-button>
          </div>
        </div>
      </div>
    </section>

    <!-- 关于我们 -->
    <section id="about" class="section section-alt">
      <div class="container">
        <h2 class="section-title">关于我们</h2>
        <div class="about-content">
          <div class="about-text">
            <p>AI实验室成立于2015年，是一个致力于人工智能技术研究与应用的创新平台。我们汇聚了一批优秀的研究人员和学生，共同探索人工智能领域的前沿问题。</p>
            <p>实验室主要研究方向包括计算机视觉、自然语言处理、数据挖掘和机器人技术等。我们与多家企业和研究机构保持紧密合作，将研究成果转化为实际应用。</p>
            <p>我们欢迎对人工智能技术有兴趣的学生和研究人员加入我们的团队，共同推动人工智能技术的发展与应用。</p>
          </div>
          <div class="contact-info">
            <h3>联系方式</h3>
            <p><el-icon><Location /></el-icon> 地址：科技大学A栋305室</p>
            <p><el-icon><Phone /></el-icon> 电话：123-4567-8910</p>
            <p><el-icon><Message /></el-icon> 邮箱：ailab@example.com</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="container">
        <p class="copyright">© 2023 AI实验室管理系统 版权所有</p>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.home-page {
  min-height: 100%;
}

/* 导航栏样式 */
.header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 64px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
}

.nav-list {
  display: flex;
  list-style: none;
}

.nav-item {
  margin: 0 15px;
}

.nav-link {
  text-decoration: none;
  color: #333;
  font-size: 16px;
  transition: color 0.3s;
}

.nav-link:hover {
  color: #409EFF;
}

/* 横幅样式 */
.banner {
  height: 500px;
  background: linear-gradient(135deg, #409EFF 0%, #36D1DC 100%);
  color: white;
  display: flex;
  align-items: center;
  margin-top: 64px;
}

.banner-content {
  max-width: 600px;
  padding: 0 20px;
}

.banner-title {
  font-size: 2.5rem;
  margin-bottom: 20px;
}

.banner-description {
  font-size: 1.2rem;
  margin-bottom: 30px;
  opacity: 0.9;
}

/* 通用部分样式 */
.section {
  padding: 80px 0;
}

.section-alt {
  background-color: #f0f5ff;
}

.section-title {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 20px;
  color: #333;
}

.section-description {
  text-align: center;
  max-width: 700px;
  margin: 0 auto 50px;
  color: #666;
  font-size: 1.1rem;
}

/* 设备展示样式 */
.equipment-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 30px;
}

.equipment-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
}

.equipment-card:hover {
  transform: translateY(-5px);
}

.equipment-image img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.equipment-info {
  padding: 20px;
}

.equipment-name {
  font-size: 1.2rem;
  margin-bottom: 10px;
  color: #333;
}

.equipment-description {
  color: #666;
  line-height: 1.5;
}

/* 项目展示样式 */
.projects-showcase {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 30px;
}

.project-item {
  background: white;
  padding: 30px 20px;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.project-icon {
  font-size: 40px;
  color: #409EFF;
  margin-bottom: 20px;
}

.project-title {
  font-size: 1.2rem;
  margin-bottom: 15px;
  color: #333;
}

.project-description {
  color: #666;
  line-height: 1.5;
}

/* 文章列表样式 */
.article-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 30px;
}

.article-card {
  background: white;
  padding: 25px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.article-title {
  font-size: 1.3rem;
  margin-bottom: 15px;
  color: #333;
}

.article-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  color: #999;
  font-size: 0.9rem;
}

.article-summary {
  color: #666;
  line-height: 1.6;
  margin-bottom: 15px;
}

/* 关于我们样式 */
.about-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 50px;
}

.about-text p {
  margin-bottom: 20px;
  line-height: 1.6;
  color: #555;
}

.contact-info {
  background: white;
  padding: 25px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.contact-info h3 {
  margin-bottom: 20px;
  color: #333;
}

.contact-info p {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  color: #555;
}

.contact-info .el-icon {
  margin-right: 10px;
  color: #409EFF;
}

/* 页脚样式 */
.footer {
  background-color: #333;
  color: white;
  padding: 30px 0;
  text-align: center;
}

.copyright {
  opacity: 0.8;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .about-content {
    grid-template-columns: 1fr;
  }
  
  .banner-title {
    font-size: 2rem;
  }
  
  .banner-description {
    font-size: 1rem;
  }
  
  .section {
    padding: 60px 0;
  }
}
</style>