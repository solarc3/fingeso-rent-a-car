import {defineStore} from 'pinia';
import {useUsuarioService} from '@/services/UsuarioService';
import router from '@/router'; // Importar router si no lo tienes ya

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
      sessionStorage.setItem('auth', JSON.stringify({
        user: this.user,
        isAuthenticated: true
      }));
    },
    clearAuth() {
      this.user = null;
      this.isAuthenticated = false;
      sessionStorage.removeItem('auth');
    },
    logout() {
      // 1. Limpiar el estado de Pinia
      this.clearAuth();
      router.push('/');

      // 2. Limpiar sessionStorage
      //sessionStorage.clear(); // Limpia todo el sessionStorage
      // O si prefieres ser más específico:
      // sessionStorage.removeItem('auth');

      // 3. Limpiar localStorage si tienes algo guardado ahí
      // localStorage.clear();
      // O específico:
      // localStorage.removeItem('userPreferences');


      // 5. Resetear otros stores si es necesario
      // Ejemplo: resetear store de carrito de compras
      // const cartStore = useCartStore();
      // cartStore.$reset();

      // 6. Redireccionar a la página de inicio
      //router.push('/');

      // 7. Opcional: Recargar la pagina para limpiar la memoria
      //window.location.reload();
    },

    initializeAuth() {
      try {
        const auth = sessionStorage.getItem('auth');
        if (auth) {
          const {user, isAuthenticated} = JSON.parse(auth);
          this.user = user;
          this.isAuthenticated = isAuthenticated;
        }
      } catch (error) {
        console.error('Error initializing auth:', error);
        this.logout(); // Si hay error, hacer logout por seguridad
      }
    }
  }
});
