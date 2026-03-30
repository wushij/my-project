import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || '')

  const setUser = (userData) => {
    user.value = userData.user
    token.value = userData.token
    localStorage.setItem('token', userData.token)
  }

  const clearUser = () => {
    user.value = null
    token.value = ''
    localStorage.removeItem('token')
  }

  const isLoggedIn = () => {
    return !!token.value
  }

  return {
    user,
    token,
    setUser,
    clearUser,
    isLoggedIn
  }
})
