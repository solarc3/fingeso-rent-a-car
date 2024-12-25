import { defineStore } from 'pinia';
import axiosInstance from '@/services/axiosConfig';

export const useSucursalStore = defineStore('sucursal', {
  state: () => ({
    sucursales: [],
    loading: false,
    error: null,
  }),

  actions: {
    async fetchSucursales() {
      this.loading = true;
      try {
        const { data } = await axiosInstance.get('/api/sucursal/listar');
        this.sucursales = data;
      } catch (error) {
        this.error = error.message || 'Error al cargar sucursales';
      } finally {
        this.loading = false;
      }
    },
  },
});