import {defineStore} from 'pinia';
import {useUsuarioService} from '@/services/UsuarioService';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    isAuthenticated: false,
    token: null
  }),

  getters: {
    isLoggedIn: (state) => state.isAuthenticated,
    currentUser: (state) => state.user,
    isAdmin: (state) => state.user?.rol === 'ADMINISTRADOR',
    isWorker: (state) => state.user?.rol === 'TRABAJADOR',
    isRenter: (state) => state.user?.rol === 'ARRENDATARIO'
  },

  actions: {
    async login(rut, password, rol) {
      try {
        const usuarioService = useUsuarioService();
        this.user = await usuarioService.login(rut, password, rol);
        this.isAuthenticated = true;

        localStorage.setItem('auth', JSON.stringify({
          user: this.user,
          isAuthenticated: true
        }));

        return true;
      } catch (error) {
        console.error('Error en login:', error);
        throw error;
      }
    },

    // Acción para cerrar sesión
    logout() {
      this.user = null
      this.token = null
      this.isAuthenticated = false

      // Limpiar localStorage
      localStorage.removeItem('auth')
    },

    // Acción para inicializar el estado desde localStorage
    initializeAuth() {
      const auth = localStorage.getItem('auth')
      if (auth) {
        const {user, token, isAuthenticated} = JSON.parse(auth)
        this.user = user
        this.token = token
        this.isAuthenticated = isAuthenticated
      }
    }
  }
})
