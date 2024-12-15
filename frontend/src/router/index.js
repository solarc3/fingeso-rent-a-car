import {createRouter, createWebHistory} from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/default.vue'),
    children: [
      {
        path: '',
        name: 'home',
        component: () => import('@/pages/Home.vue')
      },
      {
        path: 'about',
        name: 'about',
        component: () => import('@/pages/About.vue')
      },
      {
        path: 'payment',
        name: 'payment',
        component: () => import('@/pages/Payment.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
