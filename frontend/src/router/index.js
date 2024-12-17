import {createRouter, createWebHistory} from 'vue-router'
import {useAuthStore} from '@/stores/auth'

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
        name: 'payment', //  nombre de la ruta
        component: () => import('@/pages/Payment.vue'),
        props: route => ({
          vehiculoId: Number(route.query.vehiculoId),
          sucursalId: Number(route.query.sucursal),
          precioArriendo: Number(route.query.precioArriendo)
        }),
        beforeEnter: (to, from, next) => {
          const {vehiculoId, sucursal, precioArriendo} = to.query;
          if (!vehiculoId || !sucursal || !precioArriendo) {
            console.warn('Faltan parámetros requeridos');
            next('/');
            return;
          }
          next();
        }
      },
      {
        path: 'admin',
        meta: {requiresAuth: true, requiresAdmin: true},
        children: [
          {
            path: 'usuarios',
            //component: () => import('@/pages/admin/UserManagement.vue')
          },
          {
            path: 'vehiculos',
            component: () => import('@/pages/admin/VehicleManagement.vue')
          },
          {
            path: 'financial',
            //component: () => import('@/pages/admin/FinancialDashboard.vue')
          }
        ]
      },
      // proteccion rutas de trabajador
      {
        path: 'trabajador',
        meta: {requiresAuth: true, requiresWorker: true},
        children: [
          {
            path: 'reservas',
            //component: () => import('@/pages/worker/ReservationManagement.vue')
          }
        ]
      },
      // rutas para usuario authed
      {
        path: 'perfil',
        meta: {requiresAuth: true},
        component: () => import('@/pages/Profile.vue')
      },
      {
        path: 'mis-reservas',
        meta: {requiresAuth: true},
        component: () => import('@/pages/MyReservations.vue')
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/pages/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.meta.requiresAdmin && !authStore.isAdmin) {
    next('/')
  } else if (to.meta.requiresWorker && !authStore.isWorker) {
    next('/')
  } else {
    next()
  }
})

export default router
