// stores/searchStore.js
import {defineStore} from 'pinia';
import {useMetadataStore} from './metadata';
import {watch} from 'vue'
import {useSucursalService} from "@/services/SucursalService.js";

const getDefaultDates = () => {
  const today = new Date();
  const tomorrow = new Date(today);
  tomorrow.setDate(today.getDate() + 1);
  tomorrow.setHours(0, 0, 0, 0);

  const nextDay = new Date(tomorrow);
  nextDay.setDate(tomorrow.getDate() + 1);

  return {
    inicio: tomorrow.toISOString().split('T')[0],
    fin: nextDay.toISOString().split('T')[0]
  };
};

export const useSearchStore = defineStore('search', {
  state: () => ({
    loading: false,
    sucursales: [],
    error: null,
    filters: {
      marca: null,
      sucursal: null,
      tipoVehiculo: null,
      transmision: null,
      estado: 'TODOS',
      fechas: getDefaultDates(),
      rangoPrecio: [50000, 250000],
      ordenarPor: null
    },
    dateErrors: {
      fechaRetiro: '',
      fechaDevolucion: ''
    },
    lastFilterUpdate: null,
    globalLoading: false,
    globalError: null,
  }),

  getters: {
    hasActiveFilters: (state) => {
      return Object.values(state.filters).some(value =>
        value !== null && value !== 'TODOS' &&
        (!Array.isArray(value) || value.length > 0)
      );
    },

    activeFilters: (state) => {
      const metadataStore = useMetadataStore();
      const filters = [];

      if (state.filters.marca) {
        const marca = metadataStore.marcasDisponibles.find(m => m.valor === state.filters.marca);
        filters.push({label: `Marca: ${marca?.nombre}`, key: 'marca'});
      }
      if (state.filters.tipoVehiculo) {
        const tipo = metadataStore.tiposVehiculo.find(t => t.valor === state.filters.tipoVehiculo);
        filters.push({label: `Tipo: ${tipo?.texto}`, key: 'tipoVehiculo'});
      }
      if (state.filters.transmision) {
        const trans = metadataStore.tiposTransmision.find(t => t.valor === state.filters.transmision);
        filters.push({label: `Transmisión: ${trans?.texto}`, key: 'transmision'});
      }
      if (state.filters.estado && state.filters.estado !== 'TODOS') {
        filters.push({label: `Estado: Solo Disponibles`, key: 'estado'});
      }
      if (state.filters.sucursal) {
        filters.push({label: `Sucursal: ${state.filters.sucursal.nombre}`, key: 'sucursal'});
      }
      if (state.filters.ordenarPor) {
        const orden = metadataStore.opcionesOrdenamiento.find(o => o.valor === state.filters.ordenarPor);
        filters.push({label: `Ordenar por: ${orden?.texto}`, key: 'ordenarPor'});
      }

      return filters;
    },
    minDate: () => {
      const tomorrow = new Date();
      tomorrow.setDate(tomorrow.getDate() + 1);
      return tomorrow.toISOString().split('T')[0];
    },

    minDevolutionDate: (state) => {
      if (!state.filters.fechas.inicio) {
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        return tomorrow.toISOString().split('T')[0];
      }
      return state.filters.fechas.inicio;
    },
  },

  actions: {
    initializeFechas() {
      this.filters.fechas = getDefaultDates();
    },
    resetGlobalState() {
      this.globalError = null;
      this.globalLoading = false;
    },
    updateAllFilters(searchData) {
      console.log('Updating all filters:', searchData);
      this.filters = {
        ...this.filters,
        ...searchData
      };
      this.validateDates();
    },
    setGlobalLoadingState(loading) {
      this.globalLoading = loading;
    },

    setGlobalError(error) {
      this.globalError = error;
    },
    validateForm() {
      return !this.dateErrors.fechaRetiro &&
        !this.dateErrors.fechaDevolucion &&
        this.filters.fechas.inicio &&
        this.filters.fechas.fin;
    },
    async loadSucursales() {
      this.loading = true;
      try {
        const sucursalService = useSucursalService();
        this.sucursales = await sucursalService.listarSucursales();
      } catch (error) {
        console.error('Error loading sucursales:', error);
        this.error = 'Error al cargar sucursales';
      } finally {
        this.loading = false;
      }
    },
    initializeFilters() {
      const metadataStore = useMetadataStore();
      if (!this.filters.rangoPrecio) {
        this.filters.rangoPrecio = [metadataStore.precioMinimo, metadataStore.precioMaximo];
      }
    },

    updateFilter(key, value) {
      this.filters[key] = value;
      this.validateDates();
    },

    validateDates() {
      this.dateErrors = {
        fechaRetiro: '',
        fechaDevolucion: ''
      };

      const now = new Date();
      now.setHours(0, 0, 0, 0);

      const minPickup = new Date(now);
      minPickup.setDate(minPickup.getDate() + 1);

      const fechaRetiro = new Date(this.filters.fechas.inicio);
      const fechaDevolucion = new Date(this.filters.fechas.fin);

      // Validar fecha de retiro
      if (fechaRetiro < minPickup) {
        this.filters.fechas.inicio = minPickup.toISOString().split('T')[0];
        this.dateErrors.fechaRetiro = 'La fecha de retiro debe ser a partir de mañana';
        return false;
      }

      // Validar fecha de devolución
      const maxDate = new Date(fechaRetiro);
      maxDate.setDate(maxDate.getDate() + 30);

      if (fechaDevolucion <= fechaRetiro) {
        const minDevolucion = new Date(fechaRetiro);
        minDevolucion.setDate(minDevolucion.getDate() + 1);
        this.filters.fechas.fin = minDevolucion.toISOString().split('T')[0];
        this.dateErrors.fechaDevolucion = 'La fecha de devolución debe ser posterior a la fecha de retiro';
        return false;
      }

      if (fechaDevolucion > maxDate) {
        this.filters.fechas.fin = maxDate.toISOString().split('T')[0];
        this.dateErrors.fechaDevolucion = 'El período máximo de alquiler es de 30 días';
        return false;
      }

      return true;
    },

    resetFilters() {
      const metadataStore = useMetadataStore();

      this.filters = {
        marca: null,
        sucursal: null,
        tipoVehiculo: null,
        transmision: null,
        estado: 'TODOS',
        fechas: getDefaultDates(),
        rangoPrecio: [metadataStore.precioMinimo, metadataStore.precioMaximo],
        ordenarPor: null
      };

      this.dateErrors = {
        fechaRetiro: '',
        fechaDevolucion: ''
      };
    }, setupWatchers() {
      watch(
        () => this.filters,
        (newFilters, oldFilters) => {
          if (JSON.stringify(newFilters) !== JSON.stringify(oldFilters)) {
            // Disparar la actualización de filtros
            this.validateDates();
          }
        },
        {deep: true}
      );
    },

    removeFilter(filterKey) {
      const metadataStore = useMetadataStore();
      if (filterKey === 'rangoPrecio') {
        this.filters.rangoPrecio = [metadataStore.precioMinimo, metadataStore.precioMaximo];
      } else {
        this.filters[filterKey] = null;
      }
    }

  }
});
