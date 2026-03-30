<template>
  <div class="orders-page">
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
            <n-button text @click="$router.push('/login')">登录</n-button>
          </template>
          <n-button type="primary" round>下载</n-button>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="main">
      <div class="container">
        <div class="page-header">
          <h1>我的订单</h1>
          <n-button @click="$router.push('/pricing')">购买套餐</n-button>
        </div>

        <!-- Loading -->
        <div v-if="loading" class="loading-container">
          <n-spin size="large" />
        </div>

        <!-- Empty -->
        <div v-else-if="orders.length === 0" class="empty-container">
          <n-empty description="暂无订单">
            <template #extra>
              <n-button type="primary" @click="$router.push('/pricing')">
                去购买
              </n-button>
            </template>
          </n-empty>
        </div>

        <!-- Order List -->
        <div v-else class="order-list">
          <div v-for="order in orders" :key="order.orderNo" class="order-card">
            <div class="order-header">
              <span class="order-no">订单号：{{ order.orderNo }}</span>
              <n-tag :type="getStatusType(order.status)" size="small">
                {{ order.statusText }}
              </n-tag>
            </div>
            <div class="order-body">
              <div class="order-info">
                <div class="plan-name">{{ order.planName }}</div>
                <div class="order-time">{{ formatTime(order.createTime) }}</div>
              </div>
              <div class="order-amount">
                <span class="price">${{ order.amount }}</span>
              </div>
            </div>
            <div class="order-footer">
              <template v-if="order.status === 0">
                <n-button size="small" @click="cancelOrderHandler(order.orderNo)">
                  取消订单
                </n-button>
                <n-button type="primary" size="small" @click="continuePay(order.orderNo)">
                  继续支付
                </n-button>
              </template>
              <template v-else-if="order.status === 1">
                <span class="pay-time">支付时间：{{ formatTime(order.payTime) }}</span>
              </template>
            </div>
          </div>

          <!-- Pagination -->
          <div class="pagination" v-if="total > pageSize">
            <n-pagination 
              v-model:page="currentPage" 
              :page-count="Math.ceil(total / pageSize)"
              @update:page="fetchOrders"
            />
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { PersonCircleOutline } from '@vicons/ionicons5'
import { useUserStore } from '../../stores/user'
import { getMyOrders, cancelOrder } from '../../api/order'

const router = useRouter()
const message = useMessage()
const userStore = useUserStore()

const orders = ref([])
const loading = ref(true)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const userOptions = [
  { label: '我的订单', key: 'orders' },
  { label: '退出登录', key: 'logout' }
]

const handleUserSelect = (key) => {
  if (key === 'logout') {
    userStore.clearUser()
    message.success('已退出登录')
    router.push('/login')
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

// 获取状态类型
const getStatusType = (status) => {
  switch (status) {
    case 0: return 'warning'
    case 1: return 'success'
    case 2: return 'default'
    case 3: return 'error'
    default: return 'default'
  }
}

// 获取订单列表
const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await getMyOrders(currentPage.value, pageSize.value)
    if (res.code === 200) {
      orders.value = res.data.list
      total.value = res.data.total
    } else {
      message.error(res.message || '获取订单列表失败')
    }
  } catch (e) {
    message.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

// 取消订单
const cancelOrderHandler = async (orderNo) => {
  try {
    const res = await cancelOrder(orderNo)
    if (res.code === 200) {
      message.success('订单已取消')
      fetchOrders()
    } else {
      message.error(res.message || '取消订单失败')
    }
  } catch (e) {
    message.error('取消订单失败')
  }
}

// 继续支付
const continuePay = (orderNo) => {
  router.push({
    path: '/payment',
    query: { orderNo }
  })
}

onMounted(() => {
  if (!userStore.isLoggedIn()) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  fetchOrders()
})
</script>

<style scoped>
.orders-page {
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
  max-width: 800px;
  margin: 0 auto;
  padding: 40px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 32px;
  font-weight: 600;
}

/* Loading & Empty */
.loading-container,
.empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

/* Order List */
.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  background: #18181c;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 12px;
  padding: 20px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.order-no {
  font-size: 12px;
  color: #94a3b8;
}

.order-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.plan-name {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 8px;
}

.order-time {
  font-size: 12px;
  color: #94a3b8;
}

.order-amount .price {
  font-size: 24px;
  font-weight: 600;
  color: #2ADB5C;
}

.order-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
}

.pay-time {
  font-size: 12px;
  color: #94a3b8;
}

/* Pagination */
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

/* Responsive */
@media (max-width: 768px) {
  .nav {
    display: none;
  }

  .container {
    padding: 20px;
  }

  .page-header h1 {
    font-size: 24px;
  }

  .order-body {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
