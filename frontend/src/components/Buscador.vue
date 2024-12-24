<template>
  <v-form @submit.prevent="handleSubmit">
    <v-container min-width="100%">
      <v-card
        class="pa-6 search-card"
        rounded="l"
      >
        <v-slide-y-transition>
          <v-alert
            v-if="error"
            type="error"
            class="mb-4"
            dismissible
            @click:close="error = null"
          >
            {{ error }}
          </v-alert>
        </v-slide-y-transition>

        <!-- filtros principales -->
        <v-row>
          <v-col
            cols="12"
            md="3"
          >
            <v-select
              v-model="filters.marca"
              :items="metadataStore.marcasDisponibles"
              label="Marca"
              item-title="nombre"
              item-value="valor"
              clearable
              outlined
              dense
              :loading="metadataStore.loading"
            />
          </v-col>

          <v-col
            cols="12"
            md="3"
          >
            <v-select
              v-model="filters.tipoVehiculo"
              :items="metadataStore.tiposVehiculo"
              label="Tipo de Vehículo"
              item-title="texto"
              item-value="valor"
              clearable
              outlined
              dense
              :loading="metadataStore.loading"
            />
          </v-col>

          <v-col
            cols="12"
            md="3"
          >
            <v-select
              v-model="filters.transmision"
              :items="metadataStore.tiposTransmision"
              label="Transmisión"
              item-title="texto"
              item-value="valor"
              clearable
              outlined
              dense
              :loading="metadataStore.loading"
            />
          </v-col>

          <v-col
            cols="12"
            md="3"
          >
            <v-select
              v-model="filters.estado"
              :items="estadosVehiculo"
              label="Mostrar"
              item-title="text"
              item-value="value"
              clearable
              outlined
              dense
            />
          </v-col>
        </v-row>

        <!-- fechas y sucursales -->
        <v-row class="mt-4">
          <v-col
            cols="12"
            md="4"
          >
            <label class="text-subtitle-2 font-weight-bold mb-2">Sucursal de retiro</label>
            <v-select
              v-model="filters.sucursal"
              :items="sucursales"
              :loading="searchStore.loading"
              :error-messages="searchStore.error"
              outlined
              dense
              required
              item-title="nombre"
              item-value="id"
              return-object
              placeholder="Selecciona tu sucursal"
            />
          </v-col>

          <v-col
            cols="12"
            md="4"
          >
            <label class="text-subtitle-2 font-weight-bold mb-2">Fecha de retiro</label>
            <v-text-field
              v-model="filters.fechas.inicio"
              type="date"
              :error-messages="dateErrors.fechaRetiro"
              :min="minDate"
              outlined
              dense
              :rules="[v => !!v || 'Fecha de retiro requerida']"
            />
          </v-col>

          <v-col
            cols="12"
            md="4"
          >
            <label class="text-subtitle-2 font-weight-bold mb-2">Fecha de devolución</label>
            <v-text-field
              v-model="filters.fechas.fin"
              type="date"
              outlined
              :error-messages="dateErrors.fechaDevolucion"
              :min="minDevolutionDate"
              dense
              :rules="[v => !!v || 'Fecha de devolución requerida']"
            />
          </v-col>
        </v-row>

        <!-- rango de precios y ordenamiento -->
        <v-row class="mt-4">
          <v-col
            cols="12"
            md="6"
          >
            <v-range-slider
              v-model="sliderRange"
              :min="metadataStore.precioMinimo"
              :max="maxPrice"
              :step="5000"
              label="Rango de Precio"
              thumb-label="always"
              class="mt-4"
            >
              <template #prepend>
                <v-text-field
                  v-model="precioMinimo"
                  type="number"
                  prefix="$"
                  hide-details
                  density="compact"
                  style="width: 120px"
                />
              </template>
              <template #append>
                <v-text-field
                  v-model="precioMaximo"
                  type="number"
                  prefix="$"
                  hide-details
                  density="compact"
                  style="width: 120px"
                />
              </template>
            </v-range-slider>
          </v-col>

          <v-col
            cols="12"
            md="3"
          >
            <v-select
              v-model="filters.ordenarPor"
              :items="metadataStore.opcionesOrdenamiento"
              label="Ordenar por"
              item-title="texto"
              item-value="valor"
              outlined
              dense
              clearable
            />
          </v-col>

          <v-col
            cols="12"
            md="3"
            class="d-flex align-center justify-end"
          >
            <v-btn
              color="white"
              x-large
              height="56"
              min-width="150"
              class="text-h6 font-weight-bold elevation-6"
              :loading="loading"
              type="submit"
            >
              <v-icon
                start
                class="mr-2"
              >
                mdi-magnify
              </v-icon>
              Buscar
            </v-btn>
          </v-col>
        </v-row>

        <!-- filtros activos -->
        <v-slide-y-transition>
          <v-row
            v-if="hasActiveFilters"
            class="mt-6"
          >
            <v-col cols="12">
              <div class="d-flex align-center flex-wrap">
                <span class="text-subtitle-2 font-weight-bold mr-4">Filtros activos:</span>
                <v-chip
                  v-for="filter in activeFilters"
                  :key="filter.key"
                  class="ma-1"
                  closable
                  variant="elevated"
                  color="primary"
                  @click:close="removeFilter(filter.key)"
                >
                  {{ filter.label }}
                </v-chip>

                <v-btn
                  color="error"
                  variant="text"
                  size="small"
                  class="ml-auto"
                  @click="resetForm"
                >
                  <v-icon
                    start
                    class="mr-2"
                  >
                    mdi-filter-off
                  </v-icon>
                  Limpiar todos los filtros
                </v-btn>
              </div>
            </v-col>
          </v-row>
        </v-slide-y-transition>
      </v-card>
    </v-container>
  </v-form>
</template>

<script setup>
import {ref, onMounted, computed, watch} from 'vue';
import {useSearchStore} from '@/stores/search';
import {useMetadataStore} from '@/stores/metadata';
import {storeToRefs} from 'pinia';
import {debounce} from 'lodash';

const emit = defineEmits(['submit']);
const props = defineProps({
  vehiculos: {
    type: Array,
    default: () => []
  }
});

const searchStore = useSearchStore();
const metadataStore = useMetadataStore();
const estadosVehiculo = [
  {text: 'Todos', value: 'TODOS'},
  {text: 'Solo Disponibles', value: 'DISPONIBLES'}
];

// Extraer todos los refs necesarios del store
const {
  filters,
  dateErrors,
  hasActiveFilters,
  activeFilters,
  sucursales,
  minDate,      // Agregar esta línea
  minDevolutionDate // Y esta línea
} = storeToRefs(searchStore);

const loading = ref(false);
const error = ref(null);


const sliderRange = computed({
  get() {
    if (!filters.value.rangoPrecio) {
      return [metadataStore.precioMinimo, metadataStore.precioMaximo];
    }
    return filters.value.rangoPrecio;
  },
  set(value) {
    if (Array.isArray(value) && value.length === 2) {
      filters.value.rangoPrecio = value;
    }
  }
});
const maxPrice = computed(() => {
  if (props.vehiculos?.length > 0) {
    const maxVehiclePrice = Math.max(...props.vehiculos.map(v => Number(v.precioArriendo)));
    return Math.max(maxVehiclePrice, metadataStore.precioMaximo);
  }
  return metadataStore.precioMaximo;
});

const handleSubmit = () => {
  if (!validateForm()) return;

  try {
    const searchData = {
      marca: filters.value.marca,
      tipoVehiculo: filters.value.tipoVehiculo,
      transmision: filters.value.transmision,
      sucursal: filters.value.sucursal,
      estado: filters.value.estado || 'TODOS',
      fechas: {
        inicio: filters.value.fechas.inicio,
        fin: filters.value.fechas.fin
      },
      precioMinimo: sliderRange.value[0],
      precioMaximo: sliderRange.value[1],
      ordenarPor: filters.value.ordenarPor
    };

    searchStore.updateAllFilters(searchData);
    emit('submit', searchData);
  } catch (error) {
    console.error('Error en handleSubmit:', error);
    searchStore.setGlobalError('Error al procesar la búsqueda');
  }
};
const validateForm = () => {
  if (!filters.value.fechas.inicio || !filters.value.fechas.fin) {
    return false;
  }
  return searchStore.validateDates();
};


const debouncedSubmit = debounce(() => {
  handleSubmit();
}, 300);


const removeFilter = (filterKey) => {
  searchStore.removeFilter(filterKey);
  debouncedSubmit();
};

const resetForm = () => {
  searchStore.resetFilters();
  debouncedSubmit();
};

// Agregar computed properties para los campos individuales del slider
const precioMinimo = computed({
  get() {
    return sliderRange.value[0];
  },
  set(value) {
    sliderRange.value = [Number(value), sliderRange.value[1]];
  }
});

const precioMaximo = computed({
  get() {
    return sliderRange.value[1];
  },
  set(value) {
    sliderRange.value = [sliderRange.value[0], Number(value)];
  }
});
const setupWatchers = () => {
  // Remover los watchers individuales anteriores

  // Usar un solo watcher para todos los cambios
  watch(
    () => ({
      marca: filters.value.marca,
      tipoVehiculo: filters.value.tipoVehiculo,
      transmision: filters.value.transmision,
      estado: filters.value.estado,
      sucursal: filters.value.sucursal,
      ordenarPor: filters.value.ordenarPor,
      fechas: filters.value.fechas,
      rangoPrecio: sliderRange.value
    }),
    (newValues) => {
      // Validar fechas si cambiaron
      if (newValues.fechas) {
        searchStore.validateDates();
      }

      // Emitir los cambios
      emit('submit', {
        ...newValues,
        precioMinimo: newValues.rangoPrecio[0],
        precioMaximo: newValues.rangoPrecio[1]
      });
    },
    {deep: true}
  );
};
onMounted(async () => {
  try {
    searchStore.setGlobalLoadingState(true);
    await Promise.all([
      searchStore.loadSucursales(),
      metadataStore.loadMetadata()
    ]);

    searchStore.initializeFilters();
    searchStore.initializeFechas();
    setupWatchers();
    handleSubmit();
  } catch (error) {
    searchStore.setGlobalError('Error al cargar datos iniciales');
  } finally {
    searchStore.setGlobalLoadingState(false);
  }
});

</script>
<style scoped>
.search-card {
  background: linear-gradient(to right, #002349, #1E88E5) !important;
  color: white !important;
}

.search-card :deep(.v-label) {
  color: rgba(255, 255, 255, 0.8) !important;
}

.search-card :deep(.v-field) {
  background: rgba(255, 255, 255, 0.1) !important;
  border-color: rgba(255, 255, 255, 0.2) !important;
}

.search-card :deep(.v-text-field .v-input__control) {
  background: rgba(255, 255, 255, 0.1) !important;
}

.search-card :deep(.v-select .v-input__control) {
  background: rgba(255, 255, 255, 0.1) !important;
}

.text-subtitle-2 {
  color: rgba(255, 255, 255, 0.9) !important;
}

.v-btn {
  text-transform: none;
  background-color: white !important;
  color: #002349 !important;
}

.search-card :deep(.v-slider-track__fill) {
  background-color: white !important;
}

.search-card :deep(.v-slider-thumb__surface) {
  border-color: white !important;
}

.search-card :deep(.v-text-field input) {
  color: white !important;
}

.v-alert {
  background-color: rgba(244, 67, 54, 0.9) !important;
  color: white !important;
}

.search-card :deep(.v-select__selection) {
  color: white !important;
}

.search-card :deep(.v-field__input) {
  color: white !important;
}

.search-card :deep(.v-icon) {
  color: rgba(255, 255, 255, 0.8) !important;
}

.search-card :deep(.v-list-item) {
  color: #002349 !important;
}

.search-card :deep(.v-list-item) {
  color: #002349 !important;
}

.search-card .text-subtitle-2 {
  color: rgba(255, 255, 255, 0.9) !important;
}

.search-card .v-chip {
  background: rgba(255, 255, 255, 0.15) !important;
  color: white !important;
  transition: all 0.3s ease;
}

.search-card .v-chip:hover {
  background: rgba(255, 255, 255, 0.2) !important;
}

.search-card .v-chip .v-chip__close {
  opacity: 0.7;
  transition: opacity 0.2s ease;
}

.search-card .v-chip .v-chip__close:hover {
  opacity: 1;
}

.v-slide-y-transition-enter-active,
.v-slide-y-transition-leave-active {
  transition: all 0.3s ease;
}

.v-slide-y-transition-enter-from,
.v-slide-y-transition-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}
</style>
