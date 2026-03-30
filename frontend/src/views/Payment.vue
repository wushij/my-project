<template>
  <div class="payment-page">
    <!-- Header -->
    <header class="header">
      <div class="header-content">
        <div class="logo">
          <router-link to="/">
            <img src="https://img.alicdn.com/imgextra/i2/O1CN01js79rH1mt5nkV0kEl_!!6000000005011-55-tps-640-180.svg" alt="Qoder Logo" />
          </router-link>
        </div>
        <div class="header-right">
          <n-button text @click="$router.push('/pricing')">返回价格页</n-button>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="main">
      <div class="container">
        <div v-if="loading" class="loading-container">
          <n-spin size="large" />
          <p>加载订单信息...</p>
        </div>

        <div v-else-if="error" class="error-container">
          <n-result status="error" title="加载失败" :description="error">
            <template #footer>
              <n-button @click="$router.push('/pricing')">返回价格页</n-button>
            </template>
          </n-result>
        </div>

        <div v-else class="payment-content">
          <!-- Order Info -->
          <div class="order-info">
            <h1>订单支付</h1>
            <div class="order-card">
              <div class="order-item">
                <span class="label">订单号</span>
                <span class="value">{{ orderNo }}</span>
              </div>
              <div class="order-item">
                <span class="label">套餐</span>
                <span class="value">{{ orderInfo?.planName }}</span>
              </div>
              <div class="order-item total">
                <span class="label">支付金额</span>
                <span class="value price">${{ orderInfo?.amount }}</span>
              </div>
              <div class="order-item">
                <span class="label">订单有效期</span>
                <span class="value">{{ formatTime(orderInfo?.expireTime) }}</span>
              </div>
            </div>
          </div>

          <!-- QR Code -->
          <div class="qrcode-section">
            <h2>微信扫码支付</h2>
            <div v-if="qrcodeLoading" class="qrcode-loading">
              <n-spin size="medium" />
              <p>正在生成二维码...</p>
            </div>
            <div v-else-if="qrcodeError" class="qrcode-error">
              <n-result status="warning" title="二维码生成失败">
                <template #footer>
                  <n-button type="primary" @click="generateQRCode">重新生成</n-button>
                </template>
              </n-result>
            </div>
            <div v-else class="qrcode-wrapper">
              <img :src="qrcodeDataUrl" alt="支付二维码" class="qrcode-img" />
              <p class="qrcode-tip">请使用微信扫一扫支付</p>
            </div>

            <!-- Polling Status -->
            <div class="polling-status">
              <n-icon :component="TimeOutline" size="16" />
              <span>正在等待支付结果... ({{ pollingCountdown }}s)</span>
            </div>
          </div>

          <!-- Actions -->
          <div class="actions">
            <n-button @click="cancelPayment" :disabled="cancelling">
              取消支付
            </n-button>
            <n-button type="primary" @click="checkPayResult" :loading="checking">
              我已完成支付
            </n-button>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { TimeOutline } from '@vicons/ionicons5'
import QRCode from 'qrcode'
import { getOrderDetail, getOrderStatus, cancelOrder } from '../api/order'
import { createNativePay } from '../api/pay'

const route = useRoute()
const router = useRouter()
const message = useMessage()

const orderNo = ref(route.query.orderNo || '')
const orderInfo = ref(null)
const loading = ref(true)
const error = ref('')

const qrcodeDataUrl = ref('')
const qrcodeLoading = ref(false)
const qrcodeError = ref(false)

const pollingCountdown = ref(300) // 5分钟超时
const pollingTimer = ref(null)
const countdownTimer = ref(null)

const cancelling = ref(false)
const checking = ref(false)

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

// 获取订单详情
const fetchOrderInfo = async () => {
  if (!orderNo.value) {
    error.value = '订单号不存在'
    loading.value = false
    return
  }

  try {
    const res = await getOrderDetail(orderNo.value)
    if (res.code === 200) {
      orderInfo.value = res.data
      // 检查订单状态
      if (res.data.status === 1) {
        // 已支付，跳转结果页
        router.replace({
          path: '/pay-result',
          query: { orderNo: orderNo.value, status: 'success' }
        })
        return
      }
      if (res.data.status !== 0) {
        error.value = '订单状态异常'
        return
      }
      // 生成支付二维码
      await generateQRCode()
      // 开始轮询
      startPolling()
    } else {
      error.value = res.message || '获取订单信息失败'
    }
  } catch (e) {
    error.value = '获取订单信息失败'
  } finally {
    loading.value = false
  }
}

// 生成支付二维码
const generateQRCode = async () => {
  qrcodeLoading.value = true
  qrcodeError.value = false

  try {
    // 发起Native支付
    const res = await createNativePay({ orderNo: orderNo.value })
    if (res.code === 200) {
      // 生成二维码
      qrcodeDataUrl.value = await QRCode.toDataURL(res.data.codeUrl, {
        width: 256,
        margin: 2,
        color: {
          dark: '#000000',
          light: '#ffffff'
        }
      })
    } else {
      qrcodeError.value = true
      message.error(res.message || '生成支付二维码失败')
    }
  } catch (e) {
    qrcodeError.value = true
    message.error('生成支付二维码失败')
  } finally {
    qrcodeLoading.value = false
  }
}

// 开始轮询订单状态
const startPolling = () => {
  // 每3秒检查一次
  pollingTimer.value = setInterval(async () => {
    try {
      const res = await getOrderStatus(orderNo.value)
      if (res.code === 200 && res.data.status === 1) {
        // 支付成功
        stopPolling()
        router.replace({
          path: '/pay-result',
          query: { orderNo: orderNo.value, status: 'success' }
        })
      }
    } catch (e) {
      console.error('轮询订单状态失败', e)
    }
  }, 3000)

  // 倒计时
  countdownTimer.value = setInterval(() => {
    pollingCountdown.value--
    if (pollingCountdown.value <= 0) {
      stopPolling()
      message.warning('支付超时，请重新下单')
      router.push('/pricing')
    }
  }, 1000)
}

// 停止轮询
const stopPolling = () => {
  if (pollingTimer.value) {
    clearInterval(pollingTimer.value)
    pollingTimer.value = null
  }
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value)
    countdownTimer.value = null
  }
}

// 手动检查支付结果
const checkPayResult = async () => {
  checking.value = true
  try {
    const res = await getOrderStatus(orderNo.value)
    if (res.code === 200) {
      if (res.data.status === 1) {
        stopPolling()
        router.replace({
          path: '/pay-result',
          query: { orderNo: orderNo.value, status: 'success' }
        })
      } else {
        message.info('暂未收到支付结果，请稍后再试')
      }
    }
  } catch (e) {
    message.error('查询支付结果失败')
  } finally {
    checking.value = false
  }
}

// 取消支付
const cancelPayment = async () => {
  cancelling.value = true
  try {
    const res = await cancelOrder(orderNo.value)
    if (res.code === 200) {
      stopPolling()
      message.success('订单已取消')
      router.push('/pricing')
    } else {
      message.error(res.message || '取消订单失败')
    }
  } catch (e) {
    message.error('取消订单失败')
  } finally {
    cancelling.value = false
  }
}

onMounted(() => {
  fetchOrderInfo()
})

onUnmounted(() => {
  stopPolling()
})
</script>

<style scoped>
.payment-page {
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
  justify-content: space-between;
}

.logo img {
  height: 32px;
}

/* Main */
.main {
  padding: 60px 40px;
}

.container {
  max-width: 600px;
  margin: 0 auto;
}

.loading-container,
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  gap: 16px;
}

.loading-container p {
  color: #94a3b8;
}

/* Payment Content */
.payment-content {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

/* Order Info */
.order-info h1 {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 24px;
  text-align: center;
}

.order-card {
  background: #18181c;
  border-radius: 12px;
  padding: 24px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.order-item:last-child {
  border-bottom: none;
}

.order-item .label {
  color: #94a3b8;
  font-size: 14px;
}

.order-item .value {
  font-size: 14px;
  color: #fff;
}

.order-item.total .value.price {
  font-size: 24px;
  font-weight: 600;
  color: #2ADB5C;
}

/* QR Code */
.qrcode-section {
  text-align: center;
}

.qrcode-section h2 {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 24px;
}

.qrcode-loading,
.qrcode-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 40px;
}

.qrcode-loading p {
  color: #94a3b8;
}

.qrcode-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.qrcode-img {
  width: 256px;
  height: 256px;
  border-radius: 8px;
  background: #fff;
  padding: 16px;
}

.qrcode-tip {
  color: #94a3b8;
  font-size: 14px;
}

.polling-status {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 24px;
  color: #94a3b8;
  font-size: 14px;
}

/* Actions */
.actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 16px;
}

/* Responsive */
@media (max-width: 768px) {
  .main {
    padding: 40px 20px;
  }

  .order-info h1 {
    font-size: 24px;
  }
}
</style>
