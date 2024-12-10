import {createRouter, createWebHistory} from 'vue-router'


const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/pages/Home.vue'),
    meta: {
      layout: 'default'
    }
  },
  {
    path: '/payment',
    name: 'Payment',
    component: () => import('@/pages/Payment.vue'),
    meta: {
      layout: 'default'
    }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
