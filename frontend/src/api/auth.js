import api from './index'

// 登录
export const login = (data) => {
  return api.post('/auth/login', data)
}

// 注册
export const register = (data) => {
  return api.post('/auth/register', data)
}

// 获取用户信息
export const getUserInfo = () => {
  return api.get('/auth/user')
}

// 登出
export const logout = () => {
  return api.post('/auth/logout')
}
