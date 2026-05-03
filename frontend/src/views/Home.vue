<template>
  <div class="payment-demo">
    <div class="container">
      <h1 class="title">微信支付演示</h1>
      <p class="desc">选择套餐并扫码支付</p>

      <div class="plan-selector">
        <n-select
          v-model:value="selectedPlanId"
          :options="planOptions"
          placeholder="请选择套餐"
          size="large"
        />
      </div>

      <n-button 
        type="primary" 
        size="large" 
        :loading="loading"
        :disabled="!selectedPlanId"
        @click="createOrder"
        class="pay-btn"
      >
        立即支付 ¥{{ selectedPlan?.price }} ({{ selectedPlan?.name }})
      </n-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { getPlans, createOrder as createOrderApi } from '../api/payment'

const router = useRouter()
const message = useMessage()

const loading = ref(false)
const plans = ref([])
const selectedPlanId = ref(1) // 默认选择Pro套餐

// 套餐选项（用于下拉框）
const planOptions = computed(() => {
  return plans.value
    .filter(plan => plan.status === 1 && plan.price > 0) // 只显示上架且有价格的套餐
    .map(plan => ({
      label: `${plan.name} - ¥${plan.price} (${plan.credits} Credits)`,
      value: plan.id
    }))
})

// 当前选中的套餐
const selectedPlan = computed(() => {
  return plans.value.find(p => p.id === selectedPlanId.value)
})

// 获取套餐列表
const fetchPlans = async () => {
  try {
    const res = await getPlans()
    if (res.code === 200) {
      plans.value = res.data
    }
  } catch (e) {
    console.error('获取套餐列表失败:', e)
    message.error('获取套餐列表失败')
  }
}

// 创建订单
const createOrder = async () => {
  if (!selectedPlanId.value) {
    message.warning('请先选择套餐')
    return
  }
  
  loading.value = true
  try {
    const res = await createOrderApi({
      planId: selectedPlanId.value
    })
    
    if (res.code === 200) {
      router.push({
        path: '/payment',
        query: { orderNo: res.data.orderNo }
      })
    } else {
      message.error(res.message || '创建订单失败')
    }
  } catch (e) {
    console.error('创建订单失败:', e)
    message.error('创建订单失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchPlans()
})
</script>

<style scoped>
.payment-demo {
  min-height: 100vh;
  background: #090A0B;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.container {
  max-width: 500px;
  width: 100%;
  text-align: center;
}

.title {
  font-size: 36px;
  font-weight: 600;
  margin-bottom: 16px;
}

.desc {
  color: #94a3b8;
  font-size: 16px;
  margin-bottom: 32px;
}

.plan-selector {
  margin-bottom: 24px;
}

.pay-btn {
  width: 100%;
  height: 56px;
  font-size: 18px;
  font-weight: 500;
}
</style>
