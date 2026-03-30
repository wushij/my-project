import api from './index'

// 发起Native支付
export const createNativePay = (data) => {
  return api.post('/pay/native', data)
}

// 主动查询支付结果
export const queryPayResult = (orderNo) => {
  return api.get(`/pay/query/${orderNo}`)
}
