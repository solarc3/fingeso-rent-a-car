import {useVehiculoService} from '@/services/VehiculoService';
import {useMantenimientoService} from "@/services/MantenimientoService.js";
import {defineStore} from 'pinia';
import {useSearchStore} from './search';
import {watch} from 'vue';

export const useVehicleStore = defineStore('vehicle', {
  state: () => ({
    vehicles: [],
    filteredVehicles: [],
    loading: false,
    error: null,
    estadosMap: {
      'DISPONIBLE': 'Disponible',
      'NO_DISPONIBLE': 'No Disponible',
      'EN_MANTENCION': 'En Mantención',
      'EN_REPARACION': 'En Reparación',
      'ARRENDADO': 'Arrendado'
    }

  }),

  actions: {
    setupFilterWatchers() {
      const searchStore = useSearchStore();

      watch(
        () => searchStore.filters,
        (newFilters) => {
          this.applySearchFilters({
            ...newFilters,
            precioMinimo: newFilters.rangoPrecio[0],
            precioMaximo: newFilters.rangoPrecio[1]
          });
        },
        {deep: true, immediate: true}
      );
    },
    applySearchFilters(searchData) {
      this.filteredVehicles = this.vehicles.filter(vehiculo => {
        // Verificar fechas y reservas
        if (searchData.fechas?.inicio && searchData.fechas?.fin) {
          const disponible = this.isVehicleAvailableInDateRange(
            vehiculo,
            searchData.fechas.inicio,
            searchData.fechas.fin
          );
          if (!disponible) return false;
        }

        // Filtros básicos - solo aplicar si no son null
        if (searchData.marca && vehiculo.marca.toLowerCase() !== searchData.marca.toLowerCase()) return false;
        if (searchData.sucursal?.id && (!vehiculo.sucursal || vehiculo.sucursal.id !== searchData.sucursal.id)) return false;
        if (searchData.transmision && vehiculo.acriss.charAt(2) !== searchData.transmision) return false;
        if (searchData.tipoVehiculo && vehiculo.acriss.charAt(0) !== searchData.tipoVehiculo) return false;
        if (searchData.estado === 'DISPONIBLES' && vehiculo.estado !== 'DISPONIBLE') return false;

        // Rango de precio
        const precio = Number(vehiculo.precioArriendo);
        if (searchData.rangoPrecio && Array.isArray(searchData.rangoPrecio)) {
          if (precio < searchData.rangoPrecio[0] || precio > searchData.rangoPrecio[1]) return false;
        }

        return true;
      });

      // Aplicar ordenamiento si existe
      if (searchData.ordenarPor) {
        this.applySort(searchData.ordenarPor);
      }
    },

    applySort(ordenarPor) {
      switch (ordenarPor) {
        case 'PRECIO_ASC':
          this.filteredVehicles.sort((a, b) => Number(a.precioArriendo) - Number(b.precioArriendo));
          break;
        case 'PRECIO_DESC':
          this.filteredVehicles.sort((a, b) => Number(b.precioArriendo) - Number(a.precioArriendo));
          break;
        case 'MARCA_ASC':
          this.filteredVehicles.sort((a, b) => a.marca.localeCompare(b.marca));
          break;
        case 'MARCA_DESC':
          this.filteredVehicles.sort((a, b) => b.marca.localeCompare(a.marca));
          break;
      }
    },
    async getVehicleById(id) {
      if (this.vehicles.length === 0) {
        await this.fetchVehicles();
      }
      const vehicle = this.vehicles.find(v => v.id === Number(id));
      if (!vehicle) {
        throw new Error(`No se encontró vehículo con ID: ${id}`);
      }
      return vehicle;
    },
    async fetchVehicles() {
      this.loading = true;
      try {
        const vehiculoService = useVehiculoService();
        const data = await vehiculoService.obtenerVehiculos();

        if (!data || !Array.isArray(data)) {
          throw new Error('Datos de vehículos inválidos');
        }

        this.vehicles = data.map(vehiculo => ({
          ...vehiculo,
          sucursal: vehiculo.sucursal ? {
            id: vehiculo.sucursal.id,
            nombre: vehiculo.sucursal.nombre,
            direccion: vehiculo.sucursal.direccion
          } : null
        }));

        this.filteredVehicles = this.vehicles.filter(vehiculo =>
          vehiculo.estado !== 'EN_MANTENCION' &&
          vehiculo.estado !== 'EN_REPARACION'
        );

        console.log('Vehículos procesados:', this.vehicles);
        return this.vehicles;
      } catch (error) {
        console.error('Error al cargar vehículos:', error);
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },

    isVehicleAvailableInDateRange(vehiculo, fechaInicio, fechaFin) {
      if (!vehiculo.reservas || !Array.isArray(vehiculo.reservas)) {
        return true;
      }

      const inicio = new Date(fechaInicio);
      const fin = new Date(fechaFin);

      return !vehiculo.reservas.some(reserva => {
        if (!['CONFIRMADA', 'EN_PROGRESO'].includes(reserva.estado)) {
          return false;
        }
        const reservaInicio = new Date(reserva.fechaInicio);
        const reservaFin = new Date(reserva.fechaFin);

        return inicio < reservaFin && reservaInicio < fin;
      });
    },
    async createVehicle(vehicleData) {
      this.loading = true;
      try {
        const vehiculoDTO = {
          marca: vehicleData.marca,
          modelo: vehicleData.modelo,
          patente: vehicleData.patente,
          anio: vehicleData.anio || new Date().getFullYear(),
          tipoVehiculo: vehicleData.tipoVehiculo,
          transmision: vehicleData.transmision,
          precioArriendo: Number(vehicleData.precioArriendo),
          estado: vehicleData.estado || 'DISPONIBLE',
          sucursal: Number(vehicleData.sucursal)
        };

        const newVehicle = await useVehiculoService().crearVehiculo(vehiculoDTO);

        // Si el estado es EN_MANTENCION o EN_REPARACION y hay datos de falla, reportar la falla
        if ((vehicleData.estado === 'EN_MANTENCION' || vehicleData.estado === 'EN_REPARACION')
          && vehicleData.falla) {
          await this.reportarFalla({
            vehiculoId: newVehicle.id,
            tipo: vehicleData.falla.tipo,
            severidad: vehicleData.falla.severidad,
            descripcion: vehicleData.falla.descripcion,
            reportadoPorId: vehicleData.reportadoPorId || vehicleData.userId // ID del usuario que crea
          });
        }

        this.vehicles.push(newVehicle);
        return newVehicle;
      } catch (error) {
        console.error('Error creating vehicle:', error);
        throw new Error('Error al crear el vehículo: ' + error.message);
      } finally {
        this.loading = false;
      }
    },
    // Actualizar vehículo existente
    async updateVehicle(vehicleData) {
      this.loading = true;
      try {
        const vehiculoService = useVehiculoService();
        const updatedVehicle = await vehiculoService.actualizarVehiculo({
          id: vehicleData.id,
          marca: vehicleData.marca,
          modelo: vehicleData.modelo,
          patente: vehicleData.patente,
          precioArriendo: Number(vehicleData.precioArriendo),
          estado: vehicleData.estado,
          sucursal: Number(vehicleData.sucursalId)
        });

        // Si el estado cambió a EN_MANTENCION o EN_REPARACION y hay datos de falla
        if ((vehicleData.estado === 'EN_MANTENCION' || vehicleData.estado === 'EN_REPARACION')
          && vehicleData.falla) {
          await this.reportarFalla({
            vehiculoId: vehicleData.id,
            tipo: vehicleData.falla.tipo,
            severidad: vehicleData.falla.severidad,
            descripcion: vehicleData.falla.descripcion,
            reportadoPorId: vehicleData.reportadoPorId || vehicleData.userId
          });
        }

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
        const errorMessage = error.response?.data || error.message;
        throw new Error(
          errorMessage.includes('tiene reservas pendientes')
            ? 'No se puede eliminar el vehículo porque tiene reservas pendientes'
            : 'Error al eliminar el vehículo'
        );
      } finally {
        this.loading = false;
      }
    },
    async reportarFalla(fallaData) {
      this.loading = true;
      try {
        const mantenimientoService = useMantenimientoService();
        const resultado = await mantenimientoService.reportarFalla({
          vehiculoId: fallaData.vehiculoId,
          tipo: fallaData.tipo,
          severidad: fallaData.severidad,
          descripcion: fallaData.descripcion,
          reportadoPorId: fallaData.reportadoPorId
        });

        // Actualizar el estado del vehículo en la lista local
        const index = this.vehicles.findIndex(v => v.id === fallaData.vehiculoId);
        if (index !== -1) {
          this.vehicles[index] = {
            ...this.vehicles[index],
            estado: 'EN_MANTENCION'
          };
        }

        return resultado;
      } catch (error) {
        console.error('Error reportando falla:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
  },
  getters: {
    // Obtener vehículos disponibles
    availableVehicles: (state) => {
      return state.vehicles.filter(v => v.estado === 'DISPONIBLE');
    },
    getSucursalName: () => (vehiculo) => {
      if (!vehiculo) return 'No asignada';
      if (!vehiculo.sucursal) return 'No asignada';
      if (typeof vehiculo.sucursal === 'object') {
        return vehiculo.sucursal.nombre || 'No asignada';
      }
      return 'No asignada';
    },

    // Obtener vehículos en mantenimiento
    vehiclesInMaintenance: (state) => {
      return state.vehicles.filter(v =>
        v.estado === 'EN_MANTENCION' || v.estado === 'EN_REPARACION'
      );
    },
    getFilteredVehicles: (state) => {
      return state.filteredVehicles;
    }, isVehicleAvailable: (state) => (vehiculo, fechaInicio, fechaFin) => {
      if (!vehiculo.reservas || !Array.isArray(vehiculo.reservas)) return true;

      const inicio = new Date(fechaInicio);
      const fin = new Date(fechaFin);

      return !vehiculo.reservas.some(reserva => {
        const reservaInicio = new Date(reserva.fechaInicio);
        const reservaFin = new Date(reserva.fechaFin);
        return (inicio < reservaFin && reservaInicio < fin);
      });
    },

    getVehiclesByState: (state) => (estado) => {
      return state.vehicles.filter((v) => v.estado === estado);
    }, getStatusText: (state) => (estado) => {
      return state.estadosMap[estado] || estado
    }, getStatusColor: () => (estado) => {
      const colors = {
        'DISPONIBLE': 'success',
        'NO_DISPONIBLE': 'error',
        'EN_MANTENCION': 'warning',
        'EN_REPARACION': 'orange',
        'ARRENDADO': 'blue'  // Añadir color para estado arrendado
      }
      return colors[estado] || 'grey'
    }
  },

});
