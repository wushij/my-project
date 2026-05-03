import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Payment from '../views/Payment.vue'
import PayResult from '../views/PayResult.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/payment',
    name: 'Payment',
    component: Payment
  },
  {
    path: '/pay-result',
    name: 'PayResult',
    component: PayResult
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
