<template>
  <div class="pay-result-page">
    <div class="result-card">
      <!-- 成功图标 -->
      <div class="icon-wrapper success">
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
          <path d="M8 12L11 15L16 9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      
      <h1 class="title">支付成功</h1>
      <p class="desc">感谢您的购买，套餐已开通</p>
      
      <!-- 订单信息 -->
      <div class="order-info" v-if="orderInfo">
        <div class="info-item">
          <span class="label">订单号</span>
          <span class="value">{{ orderInfo.orderNo }}</span>
        </div>
        <div class="info-item">
          <span class="label">套餐</span>
          <span class="value">{{ orderInfo.planName }}</span>
        </div>
        <div class="info-item">
          <span class="label">支付金额</span>
          <span class="value price">¥{{ orderInfo.amount }}</span>
        </div>
      </div>
      
      <!-- 按钮 -->
      <div class="actions">
        <n-button type="primary" size="large" block @click="goHome">
          返回首页
        </n-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderStatus } from '../api/payment'

const route = useRoute()
const router = useRouter()

const orderInfo = ref(null)

onMounted(async () => {
  const orderNo = route.query.orderNo
  if (orderNo) {
    try {
      const res = await getOrderStatus(orderNo)
      if (res.code === 200) {
        orderInfo.value = res.data
      }
    } catch (e) {
      console.error('获取订单信息失败', e)
    }
  }
})

const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.pay-result-page {
  min-height: 100vh;
  background: #090A0B;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.result-card {
  width: 100%;
  max-width: 400px;
  background: #18181c;
  border-radius: 16px;
  padding: 48px 40px;
  text-align: center;
}

.icon-wrapper {
  width: 80px;
  height: 80px;
  margin: 0 auto 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper.success {
  background: rgba(42, 219, 92, 0.1);
  color: #2ADB5C;
}

.icon-wrapper svg {
  width: 48px;
  height: 48px;
}

.title {
  font-size: 28px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 8px;
}

.desc {
  font-size: 14px;
  color: #94a3b8;
  margin-bottom: 32px;
}

.order-info {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 32px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  color: #94a3b8;
  font-size: 14px;
}

.info-item .value {
  color: #fff;
  font-size: 14px;
}

.info-item .value.price {
  color: #2ADB5C;
  font-size: 20px;
  font-weight: 600;
}

.actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
</style>
