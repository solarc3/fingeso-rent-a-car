import {defineStore} from 'pinia';
import {useMetadataService} from '@/services/MetadataService';

export const useMetadataStore = defineStore('metadata', {
  state: () => ({
    marcasDisponibles: [],
    tiposVehiculo: [],
    tiposTransmision: [],
    loading: false,
    error: null,
    precioMinimo: 50000,
    precioMaximo: 250000,
    opcionesOrdenamiento: [
      {texto: 'Precio: Menor a Mayor', valor: 'PRECIO_ASC'},
      {texto: 'Precio: Mayor a Menor', valor: 'PRECIO_DESC'},
      {texto: 'Marca A-Z', valor: 'MARCA_ASC'},
      {texto: 'Marca Z-A', valor: 'MARCA_DESC'}
    ]
  }),

  getters: {
    getMarcaNombre: (state) => (marca) => {
      return state.marcasDisponibles.find(m => m.valor === marca)?.nombre || marca;
    },

    getTipoVehiculoNombre: (state) => (tipo) => {
      return state.tiposVehiculo.find(t => t.valor === tipo)?.texto || tipo;
    },

    getTransmisionNombre: (state) => (transmision) => {
      return state.tiposTransmision.find(t => t.valor === transmision)?.texto || transmision;
    }
  },

  actions: {
    async loadMetadata() {
      console.log('Loading metadata...');
      this.loading = true;
      try {
        const metadataService = useMetadataService();
        const [marcas, tipos, transmisiones] = await Promise.all([
          metadataService.getVehicleBrands(),
          metadataService.getVehicleTypes(),
          metadataService.getTransmissionTypes()
        ]);
        console.log('Loaded metadata:', {marcas, tipos, transmisiones});
        this.marcasDisponibles = marcas;
        this.tiposVehiculo = tipos;
        this.tiposTransmision = transmisiones;
      } catch (error) {
        console.error('Error loading metadata:', error);
        this.error = error.message;
      } finally {
        this.loading = false;
      }
    }
  }
});
