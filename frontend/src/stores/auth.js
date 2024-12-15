import {defineStore} from 'pinia';
import {useUsuarioService} from '@/services/UsuarioService';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    isAuthenticated: false,
  }),

  actions: {
    async login(rut, password, rol) {
      try {
        this.user = await useUsuarioService().login(rut, password, rol);
        this.isAuthenticated = true;

        // Usar sessionStorage para datos sensibles
        sessionStorage.setItem('auth', JSON.stringify({
          user: this.user,
          isAuthenticated: true
        }));
      } catch (error) {
        console.error('Error en login:', error);
        throw error;
      }
    },

    logout() {
      this.user = null;
      this.isAuthenticated = false;
      sessionStorage.removeItem('auth');
    },

    initializeAuth() {
      const auth = sessionStorage.getItem('auth');
      if (auth) {
        const {user, isAuthenticated} = JSON.parse(auth);
        this.user = user;
        this.isAuthenticated = isAuthenticated;
      }
    }
  }
});
