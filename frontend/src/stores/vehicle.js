import {defineStore} from 'pinia';
import {useVehiculoService} from '@/services/VehiculoService';
import {useMetadataStore} from "@/stores/metadata.js";

export const useVehicleStore = defineStore('vehicle', {
  state: () => ({
    vehicles: [],
    filteredVehicles: [],
    loading: false,
    error: null,
    filters: {
      marca: null,
      sucursal: null,
      tipoVehiculo: null,
      transmision: null,
      precioMinimo: 50000,
      precioMaximo: 250000,
      ordenarPor: null
    }
  }),

  actions: {
    // Añadir método de ordenamiento
    applySorting() {
      if (!this.filters.ordenarPor) return;

      this.filteredVehicles.sort((a, b) => {
        switch (this.filters.ordenarPor.valor) {
          case 'PRECIO_ASC':
            return a.precioArriendo - b.precioArriendo;
          case 'PRECIO_DESC':
            return b.precioArriendo - a.precioArriendo;
          case 'MARCA_ASC':
            return a.marca.localeCompare(b.marca);
          case 'MARCA_DESC':
            return b.marca.localeCompare(a.marca);
          default:
            return 0;
        }
      });
    },

    async fetchVehicles() {
      this.loading = true;
      try {
        const vehiculoService = useVehiculoService();
        const data = await vehiculoService.obtenerVehiculos();
        this.vehicles = data;
        this.filteredVehicles = [...data];
      } catch (error) {
        console.error('Error fetching vehicles:', error);
        this.error = error.message;
      } finally {
        this.loading = false;
      }
    },

    setFilters(filters) {
      console.log('Setting filters:', filters);
      this.filters = {
        marca: filters.marca,
        sucursal: filters.sucursal,
        tipoVehiculo: filters.tipoVehiculo,
        transmision: filters.transmision,
        precioMinimo: filters.precioMinimo,
        precioMaximo: filters.precioMaximo,
        ordenarPor: filters.ordenarPor
      };
      this.applyFilters();
    },

    applyFilters() {
      console.log('Applying filters with:', this.filters);
      this.filteredVehicles = this.vehicles.filter(vehiculo => {
        // Filtro por marca
        if (this.filters.marca &&
          vehiculo.marca.toUpperCase() !== this.filters.marca.toUpperCase()) {
          return false;
        }

        // Filtro por sucursal
        if (this.filters.sucursal &&
          (!vehiculo.sucursal || vehiculo.sucursal.id !== this.filters.sucursal.id)) {
          return false;
        }

        // Filtro por tipo de vehículo
        if (this.filters.tipoVehiculo &&
          !vehiculo.acriss.startsWith(this.filters.tipoVehiculo)) {
          return false;
        }

        // Filtro por transmisión
        if (this.filters.transmision &&
          vehiculo.acriss[2] !== this.filters.transmision) {
          return false;
        }

        // Filtro por precio
        const precio = Number(vehiculo.precioArriendo);
        if (precio < this.filters.precioMinimo ||
          precio > this.filters.precioMaximo) {
          return false;
        }

        return true;
      });

      // Aplicar ordenamiento si existe
      if (this.filters.ordenarPor) {
        this.applySorting();
      }

      console.log('Filtered vehicles:', this.filteredVehicles);
    },

    updateFilter(filterName, value) {
      this.filters[filterName] = value;
      this.applyFilters();
    },

    clearFilters() {
      const metadataStore = useMetadataStore();
      this.filters = {
        marca: null,
        sucursal: null,
        tipoVehiculo: null,
        transmision: null,
        precioMinimo: metadataStore.precioMinimo,
        precioMaximo: metadataStore.precioMaximo,
        ordenarPor: null
      };
      this.filteredVehicles = [...this.vehicles];
    },

    resetPriceFilter() {
      const metadataStore = useMetadataStore();
      this.filters.precioMinimo = metadataStore.precioMinimo;
      this.filters.precioMaximo = metadataStore.precioMaximo;
      this.applyFilters();
    },
    async createVehicle(vehicleData) {
      this.loading = true;
      try {
        const vehiculoService = useVehiculoService();
        const newVehicle = await vehiculoService.crearVehiculo({
          marca: vehicleData.marca,
          modelo: vehicleData.modelo,
          acriss: vehicleData.acriss || 'ECMR', // valor por defecto
          patente: vehicleData.patente,
          precioArriendo: vehicleData.precioArriendo,
          sucursalId: vehicleData.sucursalId,
          estado: vehicleData.estado || 'DISPONIBLE'
        });

        // Actualizar la lista local de vehículos
        this.vehicles.push(newVehicle);
        return newVehicle;
      } catch (error) {
        console.error('Error creating vehicle:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    // Actualizar vehículo existente
    async updateVehicle(vehicleData) {
      this.loading = true;
      try {
        const vehiculoService = useVehiculoService();
        const updatedVehicle = await vehiculoService.actualizarVehiculo(vehicleData);

        // Actualizar el vehículo en la lista local
        const index = this.vehicles.findIndex(v => v.id === vehicleData.id);
        if (index !== -1) {
          this.vehicles[index] = updatedVehicle;
        }

        return updatedVehicle;
      } catch (error) {
        console.error('Error updating vehicle:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    // Eliminar vehículo
    async deleteVehicle(vehicleId) {
      this.loading = true;
      try {
        const vehiculoService = useVehiculoService();
        await vehiculoService.eliminarVehiculo(vehicleId);

        // Eliminar el vehículo de la lista local
        this.vehicles = this.vehicles.filter(v => v.id !== vehicleId);
      } catch (error) {
        console.error('Error deleting vehicle:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    // Reportar problema con vehículo
    async reportVehicleIssue(reportData) {
      this.loading = true;
      try {
        const vehiculoService = useVehiculoService();
        const updatedVehicle = await vehiculoService.actualizarVehiculo({
          id: reportData.vehiculoId,
          estado: reportData.estado,
          // Aquí podrías agregar más campos según necesites
        });

        // Actualizar el vehículo en la lista local
        const index = this.vehicles.findIndex(v => v.id === reportData.vehiculoId);
        if (index !== -1) {
          this.vehicles[index] = updatedVehicle;
        }

        return updatedVehicle;
      } catch (error) {
        console.error('Error reporting vehicle issue:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    // Obtener vehículos por sucursal
    async fetchVehiclesBySucursal(sucursalId) {
      this.loading = true;
      try {
        const vehiculoService = useVehiculoService();
        return await vehiculoService.obtenerVehiculosDisponiblesEnSucursal(sucursalId);
      } catch (error) {
        console.error('Error fetching vehicles by sucursal:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    // Validar datos de vehículo
    validateVehicleData(vehicleData) {
      const errors = [];

      if (!vehicleData.marca) {
        errors.push('La marca es requerida');
      }

      if (!vehicleData.modelo) {
        errors.push('El modelo es requerido');
      }

      if (!vehicleData.patente) {
        errors.push('La patente es requerida');
      } else if (!/^[A-Z]{4}\d{2}$/.test(vehicleData.patente)) {
        errors.push('Formato de patente inválido (XXXX99)');
      }

      if (!vehicleData.precioArriendo || vehicleData.precioArriendo <= 0) {
        errors.push('El precio de arriendo debe ser mayor a 0');
      }

      return {
        isValid: errors.length === 0,
        errors
      };
    }
  },
  getters: {
    // Obtener vehículos disponibles
    availableVehicles: (state) => {
      return state.vehicles.filter(v => v.estado === 'DISPONIBLE');
    },

    // Obtener vehículos en mantenimiento
    vehiclesInMaintenance: (state) => {
      return state.vehicles.filter(v =>
        v.estado === 'EN_MANTENCION' || v.estado === 'EN_REPARACION'
      );
    },

    // Obtener vehículos por estado
    getVehiclesByState: (state) => (estado) => {
      return state.vehicles.filter(v => v.estado === estado);
    },

    // Obtener vehículo por ID
    getVehicleById: (state) => (id) => {
      return state.vehicles.find(v => v.id === id);
    }
  }
});
