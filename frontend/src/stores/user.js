import { defineStore } from 'pinia';
import { useUsuarioService } from '@/services/UsuarioService';

export const useUserStore = defineStore('user', {
  state: () => ({
    users: [],
    loading: false,
    error: null,
    lastDeletedUser: null
  }),

  actions: {
    async fetchUsers() {
      this.loading = true;
      try {
        const usuarioService = useUsuarioService();
        const trabajadores = await usuarioService.obtenerTrabajadores();
        const administradores = await usuarioService.obtenerAdministradores();
        const arrendatarios = await usuarioService.obtenerArrendatarios();
        this.users = [...trabajadores, ...administradores, ...arrendatarios];
      } catch (error) {
        this.error = error.message || 'Error al cargar usuarios';
      } finally {
        this.loading = false;
      }
    },

    async createUser(userData) {
      this.loading = true;
      try {
        const usuarioService = useUsuarioService();
        const newUser = await usuarioService.crearUsuario(userData);
        this.users.push(newUser);
      } catch (error) {
        this.error = error.message || 'Error al crear usuario';
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async updateUser(id, updatedData) {
      this.loading = true;
      try {
        const usuarioService = useUsuarioService();
        const updatedUser = await usuarioService.actualizarUsuario(id, updatedData);

        // Update local state
        const index = this.users.findIndex(user => user.id === id);
        if (index !== -1) {
          this.users[index] = updatedUser;
        }

        return updatedUser;
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async deleteUser(id) {
      this.loading = true;
      this.error = null;

      try {
        const usuarioService = useUsuarioService();
        await usuarioService.eliminarUsuario(id);

        // Store the deleted user temporarily in case we need to revert
        this.lastDeletedUser = this.users.find(user => user.id === id);

        // Remove user from local state
        this.users = this.users.filter(user => user.id !== id);

        return true;
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    }
  },

  getters: {
    getUserById: (state) => (id) => {
      return state.users.find(user => user.id === id);
    },
  },
});
