<template>
  <div class="pay-result-page">
    <!-- Header -->
    <header class="header">
      <div class="header-content">
        <div class="logo">
          <router-link to="/">
            <img src="https://img.alicdn.com/imgextra/i2/O1CN01js79rH1mt5nkV0kEl_!!6000000005011-55-tps-640-180.svg" alt="Qoder Logo" />
          </router-link>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="main">
      <div class="container">
        <div v-if="loading" class="loading-container">
          <n-spin size="large" />
        </div>

        <div v-else class="result-content">
          <!-- Success -->
          <template v-if="status === 'success'">
            <div class="result-icon success">
              <n-icon :component="CheckmarkCircle" size="80" color="#2ADB5C" />
            </div>
            <h1>支付成功</h1>
            <p class="desc">感谢您的购买，套餐已生效！</p>
            
            <div class="order-info" v-if="orderInfo">
              <div class="info-item">
                <span class="label">订单号</span>
                <span class="value">{{ orderInfo.orderNo }}</span>
              </div>
              <div class="info-item">
                <span class="label">套餐名称</span>
                <span class="value">{{ orderInfo.planName }}</span>
              </div>
              <div class="info-item">
                <span class="label">支付金额</span>
                <span class="value price">${{ orderInfo.amount }}</span>
              </div>
              <div class="info-item">
                <span class="label">支付时间</span>
                <span class="value">{{ formatTime(orderInfo.payTime) }}</span>
              </div>
            </div>
          </template>

          <!-- Failed -->
          <template v-else>
            <div class="result-icon failed">
              <n-icon :component="CloseCircle" size="80" color="#f56c6c" />
            </div>
            <h1>支付失败</h1>
            <p class="desc">{{ errorMessage || '支付过程中遇到问题，请重试' }}</p>
          </template>

          <!-- Actions -->
          <div class="actions">
            <n-button type="primary" size="large" @click="goHome">
              返回首页
            </n-button>
            <n-button size="large" @click="goOrders">
              查看我的订单
            </n-button>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { CheckmarkCircle, CloseCircle } from '@vicons/ionicons5'
import { getOrderDetail } from '../api/order'

const route = useRoute()
const router = useRouter()

const status = ref(route.query.status || 'success')
const orderNo = ref(route.query.orderNo || '')
const errorMessage = ref(route.query.message || '')

const loading = ref(true)
const orderInfo = ref(null)

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

// 获取订单信息
const fetchOrderInfo = async () => {
  if (!orderNo.value) {
    loading.value = false
    return
  }

  try {
    const res = await getOrderDetail(orderNo.value)
    if (res.code === 200) {
      orderInfo.value = res.data
    }
  } catch (e) {
    console.error('获取订单信息失败', e)
  } finally {
    loading.value = false
  }
}

const goHome = () => {
  router.push('/')
}

const goOrders = () => {
  router.push('/orders')
}

onMounted(() => {
  fetchOrderInfo()
})
</script>

<style scoped>
.pay-result-page {
  min-height: 100vh;
  background: #090A0B;
  color: #fff;
}

/* Header */
.header {
  padding: 16px 40px;
  background: rgba(9, 10, 11, 0.8);
  backdrop-filter: blur(12px);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo img {
  height: 32px;
}

/* Main */
.main {
  padding: 80px 40px;
}

.container {
  max-width: 500px;
  margin: 0 auto;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

/* Result Content */
.result-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.result-icon {
  margin-bottom: 24px;
}

h1 {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 12px;
}

.desc {
  color: #94a3b8;
  font-size: 16px;
  margin-bottom: 32px;
}

/* Order Info */
.order-info {
  width: 100%;
  background: #18181c;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 32px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  color: #94a3b8;
  font-size: 14px;
}

.info-item .value {
  font-size: 14px;
  color: #fff;
}

.info-item .value.price {
  font-size: 18px;
  font-weight: 600;
  color: #2ADB5C;
}

/* Actions */
.actions {
  display: flex;
  gap: 16px;
}

/* Responsive */
@media (max-width: 768px) {
  .main {
    padding: 60px 20px;
  }

  h1 {
    font-size: 24px;
  }

  .actions {
    flex-direction: column;
    width: 100%;
  }

  .actions .n-button {
    width: 100%;
  }
}
</style>
