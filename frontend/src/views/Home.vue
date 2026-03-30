<template>
  <div class="home">
    <!-- Header -->
    <header class="header">
      <div class="header-content">
        <div class="logo">
          <img src="https://img.alicdn.com/imgextra/i2/O1CN01js79rH1mt5nkV0kEl_!!6000000005011-55-tps-640-180.svg" alt="Qoder Logo" />
        </div>
        <nav class="nav">
          <a href="#" class="nav-item">产品</a>
          <a href="#" class="nav-item">企业版</a>
          <router-link to="/pricing" class="nav-item">价格</router-link>
          <a href="#" class="nav-item">博客</a>
          <a href="#" class="nav-item">资源</a>
        </nav>
        <div class="header-right">
          <template v-if="userStore.isLoggedIn()">
            <n-dropdown :options="userOptions" @select="handleUserSelect">
              <n-button text>
                <n-icon :component="PersonCircleOutline" size="18" style="margin-right: 6px;" />
                {{ userStore.user?.email }}
              </n-button>
            </n-dropdown>
          </template>
          <template v-else>
            <n-button text @click="goToLogin">登录</n-button>
          </template>
          <n-button type="primary" round @click="handleDownload">下载</n-button>
        </div>
      </div>
    </header>

    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-content">
        <div class="hero-left">
          <div class="ad-banner">
            <span class="shiny-text">Expert Panel Mode 现已上线</span>
          </div>
          <h1 class="hero-title">Qoder, 为真实软件开发打造的<br/>智能编程平台</h1>
          <div class="hero-buttons">
            <n-button type="primary" size="large" round @click="handleDownload">
              立即下载
            </n-button>
            <n-button text size="large">
              所有下载 →
            </n-button>
          </div>
        </div>
        <div class="hero-right">
          <div class="product-badge">
            <div class="badge-icon">
              <n-icon size="24" color="#FFD700" :component="TrophyOutline" />
            </div>
            <div class="badge-content">
              <div class="badge-category">PRODUCT HUNT</div>
              <div class="badge-title">当日最佳产品</div>
            </div>
          </div>
          <p class="hero-desc">深度思考，构建更好的软件<br/>Qoder 专为解决真实世界的软件开发挑战而生</p>
        </div>
      </div>

      <!-- Video Section -->
      <div class="video-section">
        <div class="mode-toggle">
          <n-button-group>
            <n-button :type="activeMode === 'editor' ? 'primary' : 'default'" @click="activeMode = 'editor'">
              编辑器模式
            </n-button>
            <n-button :type="activeMode === 'quest' ? 'primary' : 'default'" @click="activeMode = 'quest'">
              Quest模式
            </n-button>
          </n-button-group>
        </div>
        <div class="mode-content">
          <h3 class="mode-title">编辑器模式：更智能地编程</h3>
          <p class="mode-desc">
            通过智能预测来预判您的每一步操作，只需按 Tab 即可应用更改。
            或与 AI 代理并肩协作，从想法到实现一步到位。
          </p>
        </div>
        <div class="video-container">
          <video autoplay loop muted playsinline>
            <source src="https://cloud.video.taobao.com/vod/NUPrG-Mvbq3R1uzVdAor5ZyK3VDp3aWb_x9jnw_RwtI.mp4" type="video/mp4" />
          </video>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features">
      <div class="features-header">
        <h2 class="features-title">智能编程新方式</h2>
        <p class="features-subtitle">从辅助到自主，为每个编程时刻而生</p>
      </div>
      <div class="features-grid">
        <div class="feature-card" v-for="(feature, index) in features" :key="feature.title">
          <div class="feature-icon">
            <n-icon size="32" color="#2ADB5C" :component="featureIcons[index]" />
          </div>
          <h3 class="feature-title">{{ feature.title }}</h3>
          <p class="feature-desc">{{ feature.desc }}</p>
        </div>
      </div>
    </section>

    <!-- Stats Section -->
    <section class="stats">
      <div class="stats-content">
        <div class="stat-item" v-for="stat in stats" :key="stat.label">
          <div class="stat-value">{{ stat.value }}</div>
          <div class="stat-label">{{ stat.label }}</div>
        </div>
      </div>
    </section>

    <!-- Products Section -->
    <section class="products">
      <div class="products-header">
        <h2 class="products-title">按您的方式编程</h2>
        <p class="products-subtitle">多种交互方式，让您保持熟悉的工作流程</p>
      </div>
      <div class="products-grid">
        <div class="product-card" v-for="(product, index) in products" :key="product.title">
          <div class="product-icon">
            <n-icon size="40" color="#2ADB5C" :component="productIcons[index]" />
          </div>
          <h3 class="product-title">{{ product.title }}</h3>
          <p class="product-desc">{{ product.desc }}</p>
          <n-button type="primary" round size="small">{{ product.action }}</n-button>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="footer">
      <div class="footer-content">
        <div class="footer-left">
          <p class="footer-slogan">Think Deeper, Build Better</p>
          <p class="footer-copyright">© 2025 BRIGHT ZENITH PRIVATE LIMITED 版权所有</p>
        </div>
        <div class="footer-links">
          <div class="link-group">
            <h4>产品</h4>
            <a href="#">价格</a>
            <a href="#">AI-Native IDE</a>
            <a href="#">JetBrains 插件</a>
            <a href="#">CLI</a>
          </div>
          <div class="link-group">
            <h4>资源</h4>
            <a href="#">文档</a>
            <a href="#">博客</a>
            <a href="#">FAQ</a>
            <a href="#">更新日志</a>
          </div>
          <div class="link-group">
            <h4>条款</h4>
            <a href="#">服务条款</a>
            <a href="#">隐私政策</a>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, h } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { 
  FlashOutline, 
  ChatbubblesOutline, 
  HardwareChipOutline, 
  BookOutline,
  DesktopOutline,
  TerminalOutline,
  ExtensionPuzzleOutline,
  TrophyOutline,
  PersonCircleOutline,
  LogOutOutline
} from '@vicons/ionicons5'
import { useUserStore } from '../stores/user'

const router = useRouter()
const message = useMessage()
const userStore = useUserStore()
const activeMode = ref('editor')

const goToLogin = () => {
  router.push('/login')
}

const handleDownload = () => {
  window.open('https://qoder.com/download', '_blank')
}

const userOptions = [
  {
    label: '退出登录',
    key: 'logout'
  }
]

const handleUserSelect = (key) => {
  if (key === 'logout') {
    userStore.clearUser()
    message.success('已退出登录')
  }
}

const featureIcons = [FlashOutline, ChatbubblesOutline, HardwareChipOutline, BookOutline]
const productIcons = [DesktopOutline, TerminalOutline, ExtensionPuzzleOutline]

const features = [
  { title: 'Next 智能预测', desc: '预测您的意图，建议下一步代码。只需按 Tab 应用更改' },
  { title: '智能对话', desc: '通过对话与代理协作，一起规划、构建和交付' },
  { title: '自主代理', desc: '明确意图后端到端交付任务，无需持续人工指导' },
  { title: 'RepoWiki', desc: '分析代码生成结构化文档，让隐性知识显性化' }
]

const stats = [
  { value: '100k+', label: '支持的代码库文件分析' },
  { value: '26h', label: '最长代理执行时间' },
  { value: '400k+', label: 'Repo Wiki 生成的代码库文档' }
]

const products = [
  { title: 'AI-Native IDE', desc: '为真实软件开发打造的智能 IDE', action: '下载' },
  { title: 'CLI', desc: '为命令行开发者打造的智能 AI 编程工具', action: '安装' },
  { title: 'JetBrains 插件', desc: '为 JetBrains IDE 开发者打造的智能编程插件', action: '获取' }
]
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: linear-gradient(180deg, #090A0B 0%, #0f1115 100%);
}

/* Header */
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  padding: 16px 40px;
  background: rgba(9, 10, 11, 0.8);
  backdrop-filter: blur(12px);
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo img {
  height: 32px;
}

.nav {
  display: flex;
  gap: 32px;
}

.nav-item {
  color: #94a3b8;
  font-size: 14px;
  transition: color 0.2s;
}

.nav-item:hover {
  color: #fff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* Hero */
.hero {
  padding: 140px 40px 60px;
  max-width: 1400px;
  margin: 0 auto;
}

.hero-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 60px;
}

.hero-left {
  flex: 1;
}

.ad-banner {
  display: inline-block;
  padding: 8px 16px;
  background: linear-gradient(90deg, rgba(42, 219, 92, 0.2), rgba(42, 219, 92, 0.1));
  border-radius: 20px;
  margin-bottom: 24px;
}

.shiny-text {
  background: linear-gradient(90deg, #2ADB5C, #00D4AA, #2ADB5C);
  background-size: 200% auto;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: shine 3s linear infinite;
}

@keyframes shine {
  to { background-position: 200% center; }
}

.hero-title {
  font-size: 48px;
  font-weight: 600;
  line-height: 1.2;
  margin-bottom: 32px;
}

.hero-buttons {
  display: flex;
  gap: 16px;
  align-items: center;
}

.hero-right {
  width: 360px;
}

.product-badge {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  margin-bottom: 20px;
}

.badge-icon {
  font-size: 24px;
}

.badge-category {
  font-size: 12px;
  color: #94a3b8;
}

.badge-title {
  font-size: 14px;
  font-weight: 500;
}

.hero-desc {
  font-size: 16px;
  color: #94a3b8;
  line-height: 1.6;
}

/* Video Section */
.video-section {
  background: linear-gradient(180deg, rgba(42, 219, 92, 0.1), transparent);
  border-radius: 24px;
  padding: 40px;
}

.mode-toggle {
  margin-bottom: 24px;
}

.mode-title {
  font-size: 24px;
  margin-bottom: 12px;
}

.mode-desc {
  color: #94a3b8;
  margin-bottom: 24px;
  max-width: 600px;
}

.video-container {
  border-radius: 16px;
  overflow: hidden;
}

.video-container video {
  width: 100%;
  display: block;
}

/* Features */
.features {
  padding: 100px 40px;
  max-width: 1400px;
  margin: 0 auto;
}

.features-header {
  text-align: center;
  margin-bottom: 60px;
}

.features-title {
  font-size: 36px;
  margin-bottom: 12px;
}

.features-subtitle {
  font-size: 18px;
  color: #94a3b8;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.feature-card {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 16px;
  padding: 32px;
  transition: all 0.3s;
}

.feature-card:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(42, 219, 92, 0.3);
}

.feature-icon {
  font-size: 32px;
  margin-bottom: 16px;
}

.feature-title {
  font-size: 18px;
  margin-bottom: 8px;
}

.feature-desc {
  font-size: 14px;
  color: #94a3b8;
  line-height: 1.6;
}

/* Stats */
.stats {
  padding: 60px 40px;
  background: rgba(255, 255, 255, 0.02);
}

.stats-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: center;
  gap: 100px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 48px;
  font-weight: 700;
  background: linear-gradient(135deg, #2ADB5C, #00D4AA);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #94a3b8;
}

/* Products */
.products {
  padding: 100px 40px;
  max-width: 1400px;
  margin: 0 auto;
}

.products-header {
  text-align: center;
  margin-bottom: 60px;
}

.products-title {
  font-size: 36px;
  margin-bottom: 12px;
}

.products-subtitle {
  font-size: 18px;
  color: #94a3b8;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.product-card {
  background: linear-gradient(180deg, rgba(42, 219, 92, 0.1), rgba(42, 219, 92, 0.02));
  border: 1px solid rgba(42, 219, 92, 0.2);
  border-radius: 20px;
  padding: 40px;
  text-align: center;
}

.product-icon {
  font-size: 40px;
  margin-bottom: 20px;
}

.product-title {
  font-size: 24px;
  margin-bottom: 12px;
}

.product-desc {
  font-size: 14px;
  color: #94a3b8;
  margin-bottom: 24px;
}

/* Footer */
.footer {
  padding: 60px 40px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.footer-content {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
}

.footer-slogan {
  font-size: 20px;
  font-weight: 500;
  margin-bottom: 8px;
}

.footer-copyright {
  font-size: 14px;
  color: #64748b;
}

.footer-links {
  display: flex;
  gap: 80px;
}

.link-group h4 {
  font-size: 14px;
  margin-bottom: 16px;
}

.link-group a {
  display: block;
  font-size: 14px;
  color: #94a3b8;
  margin-bottom: 8px;
  transition: color 0.2s;
}

.link-group a:hover {
  color: #fff;
}

/* Responsive */
@media (max-width: 1024px) {
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .hero-content {
    flex-direction: column;
  }
  .hero-right {
    width: 100%;
    margin-top: 40px;
  }
  .hero-title {
    font-size: 32px;
  }
  .features-grid, .products-grid {
    grid-template-columns: 1fr;
  }
  .stats-content {
    flex-direction: column;
    gap: 40px;
  }
  .footer-content {
    flex-direction: column;
    gap: 40px;
  }
  .footer-links {
    gap: 40px;
    flex-wrap: wrap;
  }
  .nav {
    display: none;
  }
}
</style>
