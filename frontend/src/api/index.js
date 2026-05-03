import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8050/api',
  timeout: 10000
})

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    return Promise.reject(error)
  }
)

export default api
