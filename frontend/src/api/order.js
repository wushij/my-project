import api from './index'

// 创建订单
export const createOrder = (data) => {
  return api.post('/order/create', data)
}

// 查询订单状态
export const getOrderStatus = (orderNo) => {
  return api.get(`/order/status/${orderNo}`)
}

// 获取我的订单列表
export const getMyOrders = (page = 1, pageSize = 10) => {
  return api.get('/order/list', { params: { page, pageSize } })
}

// 获取订单详情
export const getOrderDetail = (orderNo) => {
  return api.get(`/order/${orderNo}`)
}

// 取消订单
export const cancelOrder = (orderNo) => {
  return api.post(`/order/cancel/${orderNo}`)
}
