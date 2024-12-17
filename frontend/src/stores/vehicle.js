import {defineStore} from 'pinia';
import {useVehiculoService} from '@/services/VehiculoService';
import {useMetadataStore} from "@/stores/metadata.js";
import {useMantenimientoService} from "@/services/MantenimientoService.js";

export const useVehicleStore = defineStore('vehicle', {
  state: () => ({
    vehicles: [],
    filteredVehicles: [], // Se llenará con todos los vehículos al cargar
    loading: false,
    error: null,
    filters: {
      marca: null,
      sucursal: null,
      tipoVehiculo: null,
      transmision: null,
      precioMinimo: 50000,
      precioMaximo: 250000,
      ordenarPor: null,
      disponibilidad: 'TODOS',
      fechas: {  // Agregar fechas por defecto
        inicio: new Date().toISOString(),
        fin: new Date(Date.now() + 86400000).toISOString() // mañana
      }
    }, estadosMap: {
      'DISPONIBLE': 'Disponible',
      'NO_DISPONIBLE': 'No Disponible',
      'EN_MANTENCION': 'En Mantención',
      'EN_REPARACION': 'En Reparación'
    }

  }),

  actions: {
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
    async getVehicleById(id) {
      // Si no hay vehículos cargados, cargarlos primero
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

        // Procesar los vehículos
        this.vehicles = data.map(vehiculo => ({
          ...vehiculo,
          sucursal: vehiculo.sucursal ? {
            id: vehiculo.sucursal.id,
            nombre: vehiculo.sucursal.nombre,
            direccion: vehiculo.sucursal.direccion
          } : null
        }));

        // Inicializar filteredVehicles con todos los vehículos disponibles
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

    setFilters(filters) {
      console.log('Setting filters:', filters);
      this.filters = {
        ...this.filters,
        marca: filters.marca,
        sucursal: filters.sucursal,
        tipoVehiculo: filters.tipoVehiculo,
        transmision: filters.transmision,
        precioMinimo: filters.precioMinimo,
        precioMaximo: filters.precioMaximo,
        ordenarPor: filters.ordenarPor,
        disponibilidad: filters.disponibilidad,
        fechas: filters.fechas ? {
          inicio: filters.fechas.inicio,
          fin: filters.fechas.fin
        } : null
      };
      this.applyFilters(); // Asegúrate de que esta línea esté presente
    },

    applyFilters() {
      console.log('Applying filters with:', this.filters);

      this.filteredVehicles = this.vehicles.filter(vehiculo => {
        // Excluir vehículos en mantención o reparación por defecto
        if (vehiculo.estado === 'EN_MANTENCION' || vehiculo.estado === 'EN_REPARACION') {
          return false;
        }

        // Si hay filtros específicos, aplicarlos
        if (this.filters.marca &&
          vehiculo.marca.toLowerCase() !== this.filters.marca.toLowerCase()) {
          return false;
        }

        if (this.filters.sucursal &&
          (!vehiculo.sucursal || vehiculo.sucursal.id !== this.filters.sucursal.id)) {
          return false;
        }

        if (this.filters.transmision) {
          const transmision = vehiculo.acriss.charAt(2);
          if (transmision !== this.filters.transmision) {
            return false;
          }
        }

        // Filtro por precio
        const precio = Number(vehiculo.precioArriendo);
        if (precio < this.filters.precioMinimo || precio > this.filters.precioMaximo) {
          return false;
        }

        // Verificar disponibilidad solo si hay fechas especificadas
        if (this.filters.fechas) {
          const disponible = this.isVehicleAvailableInDateRange(
            vehiculo,
            this.filters.fechas.inicio,
            this.filters.fechas.fin
          );

          if (this.filters.disponibilidad === 'DISPONIBLES' && !disponible) {
            return false;
          }
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
    isVehicleAvailableInDateRange(vehicle, startDate, endDate) {
      if (!vehicle.reservas || !Array.isArray(vehicle.reservas)) return true;

      const start = new Date(startDate);
      const end = new Date(endDate);

      return !vehicle.reservas.some(reserva => {
        const reservaStart = new Date(reserva.fechaInicio);
        const reservaEnd = new Date(reserva.fechaFin);
        return (start < reservaEnd && reservaStart < end);
      });
    },
    clearFilters() {
      const metadataStore = useMetadataStore();

      // Crear fechas por defecto
      const fechaRetiro = new Date();
      const fechaDevolucion = new Date();
      fechaDevolucion.setDate(fechaDevolucion.getDate() + 1); // Mañana

      this.filters = {
        marca: null,
        sucursal: null,
        tipoVehiculo: null,
        transmision: null,
        precioMinimo: metadataStore.precioMinimo,
        precioMaximo: metadataStore.precioMaximo,
        ordenarPor: null,
        disponibilidad: 'TODOS',
        fechas: {
          inicio: fechaRetiro.toISOString(),
          fin: fechaDevolucion.toISOString()
        }
      };

      this.filteredVehicles = this.vehicles.filter(vehiculo =>
        vehiculo.estado !== 'EN_MANTENCION' &&
        vehiculo.estado !== 'EN_REPARACION'
      );
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
        // Crear el vehículo primero
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
        'EN_REPARACION': 'orange'
      }
      return colors[estado] || 'grey'
    }
  },

});
