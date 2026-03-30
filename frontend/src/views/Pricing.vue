<template>
  <div class="pricing-page">
    <!-- Header -->
    <header class="header">
      <div class="header-content">
        <div class="logo">
          <router-link to="/">
            <img src="https://img.alicdn.com/imgextra/i2/O1CN01js79rH1mt5nkV0kEl_!!6000000005011-55-tps-640-180.svg" alt="Qoder Logo" />
          </router-link>
        </div>
        <nav class="nav">
          <a href="#" class="nav-item">产品</a>
          <a href="#" class="nav-item">企业版</a>
          <router-link to="/pricing" class="nav-item active">价格</router-link>
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
            <n-button text @click="$router.push('/login')">登录</n-button>
          </template>
          <n-button type="primary" round>下载</n-button>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="main">
      <div class="container">
        <!-- Title -->
        <div class="page-header">
          <h1>价格</h1>
          <p>选择最适合的方案，开启你的 AI 编程之旅。</p>
        </div>

        <!-- Section Header -->
        <div class="section-header">
          <h2>适用于个人</h2>
          <div class="section-actions">
            <a href="#" class="learn-more">了解更多 ↗</a>
            <div class="promo-tag">
              <n-icon :component="NotificationsOutline" size="16" />
              <span>限时 5 折优惠 ›</span>
            </div>
          </div>
        </div>

        <!-- Loading -->
        <div v-if="loading" class="loading-container">
          <n-spin size="large" />
        </div>

        <!-- Pricing Cards -->
        <div v-else class="pricing-grid">
          <div 
            v-for="plan in plans" 
            :key="plan.id" 
            class="pricing-card"
            :class="{ featured: plan.code === 'PRO' }"
          >
            <div class="card-header">
              <h3>{{ plan.name }}</h3>
              <n-tag v-if="plan.originalPrice" type="success" size="small">立省 50%</n-tag>
            </div>
            <div class="price">
              <span class="amount">${{ plan.price }}</span>
              <span class="period">/月</span>
              <span v-if="plan.originalPrice" class="original">${{ plan.originalPrice }}/月</span>
            </div>
            <p class="desc">{{ plan.code === 'FREE' ? '包括' : `包含${getPrevPlanName(plan)}的全部功能，同时：` }}</p>
            <ul class="features">
              <li v-for="(feature, index) in plan.features" :key="index">
                <n-icon :component="CheckmarkCircle" color="#2ADB5C" />
                <span>{{ feature }}</span>
              </li>
            </ul>
            <n-button 
              v-if="plan.code === 'FREE'" 
              block 
              size="large" 
              class="action-btn secondary"
            >
              <n-icon :component="DesktopOutline" />
              下载
            </n-button>
            <n-button 
              v-else
              :type="plan.code === 'PRO' ? 'primary' : undefined"
              block 
              size="large" 
              class="action-btn"
              :class="{ secondary: plan.code !== 'PRO' }"
              @click="handleSubscribe(plan)"
              :loading="subscribing === plan.id"
            >
              订阅
            </n-button>
          </div>
        </div>

        <!-- Footer Note -->
        <div class="footer-note">
          <p>Qoder 的使用以 Credits 计量。</p>
          <p>价格不含适用的税费和关税，包括增值税（VAT）和适用的销售税。</p>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { CheckmarkCircle, DesktopOutline, NotificationsOutline, PersonCircleOutline } from '@vicons/ionicons5'
import { useMessage } from 'naive-ui'
import { useUserStore } from '../stores/user'
import { getPlanList } from '../api/plan'
import { createOrder } from '../api/order'

const router = useRouter()
const message = useMessage()
const userStore = useUserStore()

const plans = ref([])
const loading = ref(true)
const subscribing = ref(null)

const userOptions = [
  { label: '我的订单', key: 'orders' },
  { label: '退出登录', key: 'logout' }
]

const handleUserSelect = (key) => {
  if (key === 'logout') {
    userStore.clearUser()
    message.success('已退出登录')
  } else if (key === 'orders') {
    router.push('/orders')
  }
}

const getPrevPlanName = (plan) => {
  if (plan.code === 'PRO') return '免费'
  if (plan.code === 'PRO_PLUS') return 'Pro'
  if (plan.code === 'ULTRA') return 'Pro'
  return ''
}

const fetchPlans = async () => {
  try {
    const res = await getPlanList()
    if (res.code === 200) {
      plans.value = res.data
    } else {
      message.error(res.message || '获取套餐列表失败')
    }
  } catch (error) {
    message.error('获取套餐列表失败')
  } finally {
    loading.value = false
  }
}

const handleSubscribe = async (plan) => {
  // 检查登录状态
  if (!userStore.isLoggedIn()) {
    message.warning('请先登录')
    router.push('/login')
    return
  }

  subscribing.value = plan.id

  try {
    const res = await createOrder({ planId: plan.id })
    if (res.code === 200) {
      // 跳转到支付页面
      router.push({
        path: '/payment',
        query: { orderNo: res.data.orderNo }
      })
    } else {
      message.error(res.message || '创建订单失败')
    }
  } catch (error) {
    message.error('创建订单失败')
  } finally {
    subscribing.value = null
  }
}

onMounted(() => {
  fetchPlans()
})
</script>

<style scoped>
.pricing-page {
  min-height: 100vh;
  background: #090A0B;
  color: #fff;
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
  text-decoration: none;
  transition: color 0.2s;
}

.nav-item:hover,
.nav-item.active {
  color: #fff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* Main */
.main {
  padding-top: 100px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px;
}

.page-header {
  margin-bottom: 48px;
}

.page-header h1 {
  font-size: 48px;
  font-weight: 600;
  margin-bottom: 12px;
}

.page-header p {
  font-size: 18px;
  color: #94a3b8;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-header h2 {
  font-size: 20px;
  font-weight: 500;
}

.section-actions {
  display: flex;
  align-items: center;
  gap: 24px;
}

.learn-more {
  color: #94a3b8;
  text-decoration: none;
  font-size: 14px;
}

.learn-more:hover {
  color: #fff;
}

.promo-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #2ADB5C;
  font-size: 14px;
}

/* Loading */
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

/* Pricing Grid */
.pricing-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 48px;
}

.pricing-card {
  background: #18181c;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  padding: 28px;
  display: flex;
  flex-direction: column;
  min-width: 0; /* 防止grid子项被内容撑大 */
}

.pricing-card.featured {
  border-color: rgba(42, 219, 92, 0.3);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.card-header h3 {
  font-size: 18px;
  font-weight: 500;
}

.price {
  display: flex;
  align-items: baseline;
  gap: 4px;
  margin-bottom: 16px;
}

.amount {
  font-size: 40px;
  font-weight: 600;
}

.period {
  font-size: 16px;
  color: #94a3b8;
}

.original {
  font-size: 14px;
  color: #666;
  text-decoration: line-through;
  margin-left: 8px;
}

.desc {
  font-size: 14px;
  color: #94a3b8;
  margin-bottom: 16px;
}

.features {
  list-style: none;
  padding: 0;
  margin: 0 0 24px 0;
  flex: 1;
}

.features li {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 14px;
  color: #e0e0e0;
  margin-bottom: 12px;
  word-break: break-word; /* 允许文字换行 */
}

.features li span {
  flex: 1;
  min-width: 0; /* 防止flex子项溢出 */
}

.features li .n-icon {
  flex-shrink: 0;
  margin-top: 2px;
}

.action-btn {
  margin-top: auto;
}

.action-btn.secondary {
  background: #2a2a2e !important;
  border-color: #2a2a2e !important;
  color: #fff !important;
}

.action-btn.secondary:hover {
  background: #3a3a3e !important;
}

/* Footer Note */
.footer-note {
  font-size: 14px;
  color: #666;
}

.footer-note p {
  margin-bottom: 4px;
}

/* Responsive */
@media (max-width: 1024px) {
  .pricing-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .nav {
    display: none;
  }
  
  .pricing-grid {
    grid-template-columns: 1fr;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .page-header h1 {
    font-size: 32px;
  }
}
</style>
