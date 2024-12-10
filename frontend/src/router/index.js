import {createRouter, createWebHistory} from 'vue-router'
import Home from '@/pages/Home.vue'
import Payment from '@/pages/Payment.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: {
      title: 'Home'
    }
  },
  {
    path: '/payment',
    name: 'Payment',
    component: Payment,
    meta: {
      title: 'Payment'
    }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
