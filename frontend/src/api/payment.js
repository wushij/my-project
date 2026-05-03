import api from './index'

// 获取套餐列表
export const getPlans = () => {
  return api.get('/plans')
}

// 创建订单
export const createOrder = (data) => {
  return api.post('/order/create', data)
}

// 发起Native支付
export const createNativePay = (data) => {
  return api.post('/pay/native', data)
}

// 查询订单状态
export const getOrderStatus = (orderNo) => {
  return api.get(`/order/status/${orderNo}`)
}

// 取消订单
export const cancelOrder = (orderNo) => {
  return api.post(`/order/cancel/${orderNo}`)
}
