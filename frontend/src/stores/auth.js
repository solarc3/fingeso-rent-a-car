import { defineStore } from 'pinia';
import { useUsuarioService } from '@/services/UsuarioService';
import router from '@/router';
import { encrypt, decrypt } from '@/utils/crypto';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    isAuthenticated: false,
  }),

  getters: {
    isAdmin: (state) => state.user?.rol === 'ADMINISTRADOR',
    isWorker: (state) => state.user?.rol === 'TRABAJADOR',
    isRenter: (state) => state.user?.rol === 'ARRENDATARIO',
  },

  actions: {
    async login(rut, password, rol) {
      try {
        const usuario = await useUsuarioService().login(rut, password, rol);
        this.setUser(usuario);
        return usuario;
      } catch (error) {
        this.logout();
        throw error;
      }
    },

    setUser(user) {
      this.user = user;
      this.isAuthenticated = true;
      // Encriptar datos antes de guardar
      const encryptedData = encrypt({
        user: this.user,
        isAuthenticated: true
      });
      sessionStorage.setItem('auth', encryptedData);
    },

    clearAuth() {
      this.user = null;
      this.isAuthenticated = false;
      sessionStorage.removeItem('auth');
    },

    logout() {
      this.clearAuth();
      router.push('/');
    },

    initializeAuth() {
      try {
        const encryptedAuth = sessionStorage.getItem('auth');
        if (encryptedAuth) {

          const decryptedData = decrypt(encryptedAuth);
          if (decryptedData) {
            this.user = decryptedData.user;
            this.isAuthenticated = decryptedData.isAuthenticated;
          }
        }
      } catch (error) {
        console.error('Error initializing auth:', error);
        this.logout();
      }
    }
  }
});
