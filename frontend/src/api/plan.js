import api from './index'

// 获取套餐列表
export const getPlanList = () => {
  return api.get('/plan/list')
}

// 获取套餐详情
export const getPlanDetail = (id) => {
  return api.get(`/plan/${id}`)
}
