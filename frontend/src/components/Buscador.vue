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

        <!-- Filtros principales -->
        <v-row>
          <v-col
            cols="12"
            md="3"
          >
            <v-select
              v-model="formData.marca"
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
              v-model="formData.tipoVehiculo"
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
              v-model="formData.transmision"
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
              v-model="formData.estado"
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

        <!-- Fechas y Sucursales -->
        <v-row class="mt-4">
          <v-col
            cols="12"
            md="4"
          >
            <label class="text-subtitle-2 font-weight-bold mb-2">Sucursal de retiro</label>
            <v-select
              v-model="formData.sucursal"
              :items="sucursales"
              :loading="loading"
              :error-messages="error"
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
            <div class="d-flex">
              <v-text-field
                v-model="formData.fechaRetiro"
                type="date"
                :error-messages="dateErrors.fechaRetiro"
                :min="minDate"
                outlined
                dense
                class="mr-2 flex-grow-1"
                :rules="[v => !!v || 'Fecha de retiro requerida']"
              />
              <v-select
                v-model="formData.horaRetiro"
                :items="horasDisponibles"
                :error-messages="dateErrors.horaRetiro"
                outlined
                dense
                style="max-width: 120px"
                :rules="[v => !!v || 'Hora requerida']"
              />
            </div>
          </v-col>

          <v-col
            cols="12"
            md="4"
          >
            <label class="text-subtitle-2 font-weight-bold mb-2">Fecha de devolución</label>
            <div class="d-flex">
              <v-text-field
                v-model="formData.fechaDevolucion"
                type="date"
                outlined
                :error-messages="dateErrors.fechaDevolucion"
                :min="minDevolutionDate"
                dense
                class="mr-2 flex-grow-1"
                :rules="[v => !!v || 'Fecha de devolución requerida']"
              />
              <v-select
                v-model="formData.horaDevolucion"
                :items="horasDisponibles"
                outlined
                dense
                style="max-width: 120px"
                :rules="[v => !!v || 'Hora requerida']"
              />
            </div>
          </v-col>
        </v-row>

        <!-- Rango de precios y ordenamiento -->
        <v-row class="mt-4">
          <v-col
            cols="12"
            md="6"
          >
            <v-range-slider
              v-model="formData.rangoPrecio"
              :min="metadataStore.precioMinimo"
              :max="metadataStore.precioMaximo"
              :step="5000"
              label="Rango de Precio"
              thumb-label="always"
              class="mt-4"
            >
              <template #prepend>
                <v-text-field
                  v-model="formData.rangoPrecio[0]"
                  type="number"
                  prefix="$"
                  hide-details
                  density="compact"
                  style="width: 120px"
                />
              </template>
              <template #append>
                <v-text-field
                  v-model="formData.rangoPrecio[1]"
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
              v-model="formData.ordenarPor"
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

        <!-- Filtros activos -->
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
import {ref, reactive, onMounted, watch, computed} from 'vue';
import {useMetadataStore} from '@/stores/metadata';
import {useSucursalService} from '@/services/SucursalService';
import {debounce} from 'lodash';

const emit = defineEmits(['submit']);

const metadataStore = useMetadataStore();

// Estado
const loading = ref(false);
const error = ref(null);
const sucursales = ref([]);
const formValid = ref(true);
const dateErrors = ref({
  fechaRetiro: '',
  fechaDevolucion: '',
  horaRetiro: '',
  horaDevolucion: ''
});

// Datos del formulario
const formData = reactive({
  marca: null,
  sucursal: null, // Mantener null para mostrar todas las sucursales
  tipoVehiculo: null,
  transmision: null,
  estado: 'TODOS',
  fechaRetiro: new Date().toISOString().split('T')[0], // Fecha actual
  horaRetiro: '09:00', // Hora por defecto
  fechaDevolucion: (() => {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    return tomorrow.toISOString().split('T')[0];
  })(), // Fecha de mañana
  horaDevolucion: '09:00', // Hora por defecto
  rangoPrecio: [metadataStore.precioMinimo, metadataStore.precioMaximo],
  ordenarPor: null
});

// Constantes
const horasDisponibles = [
  '08:30', '09:00', '09:30', '10:00', '10:30',
  '11:00', '11:30', '12:00', '12:30', '13:00',
  '13:30', '14:00', '14:30', '15:00', '15:30',
  '16:00', '16:30', '17:00', '17:30'
];

const estadosVehiculo = [
  {text: 'Todos', value: 'TODOS'},
  {text: 'Solo Disponibles', value: 'DISPONIBLES'}
];

// Computed
const minDate = computed(() => {
  const today = new Date();
  return today.toISOString().split('T')[0];
});

const minDevolutionDate = computed(() => {
  return formData.fechaRetiro || minDate.value;
});
const activeFilters = computed(() => {
  const filters = [];

  if (formData.marca) {
    const marca = metadataStore.marcasDisponibles.find(m => m.valor === formData.marca);
    filters.push({label: `Marca: ${marca?.nombre}`, key: 'marca'});
  }
  if (formData.tipoVehiculo) {
    const tipo = metadataStore.tiposVehiculo.find(t => t.valor === formData.tipoVehiculo);
    filters.push({label: `Tipo: ${tipo?.texto}`, key: 'tipoVehiculo'});
  }
  if (formData.transmision) {
    const trans = metadataStore.tiposTransmision.find(t => t.valor === formData.transmision);
    filters.push({label: `Transmisión: ${trans?.texto}`, key: 'transmision'});
  }
  if (formData.estado && formData.estado !== 'TODOS') {
    const estado = estadosVehiculo.find(e => e.value === formData.estado);
    filters.push({label: `Estado: ${estado?.text}`, key: 'estado'});
  }
  if (formData.sucursal) {
    filters.push({label: `Sucursal: ${formData.sucursal.nombre}`, key: 'sucursal'});
  }
  if (formData.ordenarPor) {
    const orden = metadataStore.opcionesOrdenamiento.find(o => o.valor === formData.ordenarPor);
    filters.push({label: `Ordenar por: ${orden?.texto}`, key: 'ordenarPor'});
  }

  return filters;
});
watch(() => formData.marca, () => debouncedSubmit());
watch(() => formData.tipoVehiculo, () => debouncedSubmit());
watch(() => formData.transmision, () => debouncedSubmit());
watch(() => formData.estado, () => debouncedSubmit());
watch(() => formData.sucursal, () => debouncedSubmit());
watch(() => formData.ordenarPor, () => debouncedSubmit());
watch(() => formData.rangoPrecio, () => debouncedSubmit(), {deep: true});
const hasActiveFilters = computed(() => activeFilters.value.length > 0);

// Watchers para validación y actualización automática
watch(() => formData.fechaRetiro, (newDate) => {
  validateDates();
  debouncedSubmit();
});

watch(() => formData.fechaDevolucion, (newDate) => {
  validateDates();
  debouncedSubmit();
});

watch(() => formData.horaRetiro, (newTime) => {
  validateTimes();
  debouncedSubmit();
});

watch(() => formData.horaDevolucion, (newTime) => {
  validateTimes();
  debouncedSubmit();
});
// Watchers para validación de fechas
watch(() => formData.fechaRetiro, (newDate) => {
  validateDates();
});

watch(() => formData.fechaDevolucion, (newDate) => {
  validateDates();
});


watch(() => formData.horaDevolucion, (newTime) => {
  validateTimes();
});
const removeFilter = (filterKey) => {
  if (filterKey === 'rangoPrecio') {
    formData.rangoPrecio = [metadataStore.precioMinimo, metadataStore.precioMaximo];
  } else {
    formData[filterKey] = null;
  }
  debouncedSubmit();
};

const resetForm = () => {
  // Crear fechas por defecto
  const fechaRetiro = new Date();
  const fechaDevolucion = new Date();
  fechaDevolucion.setDate(fechaDevolucion.getDate() + 1);

  Object.assign(formData, {
    marca: null,
    sucursal: null,
    tipoVehiculo: null,
    transmision: null,
    estado: 'TODOS',
    fechaRetiro: fechaRetiro.toISOString().split('T')[0],
    horaRetiro: '09:00',
    fechaDevolucion: fechaDevolucion.toISOString().split('T')[0],
    horaDevolucion: '09:00',
    rangoPrecio: [metadataStore.precioMinimo, metadataStore.precioMaximo],
    ordenarPor: null
  });

  error.value = null;
  dateErrors.value = {
    fechaRetiro: '',
    fechaDevolucion: '',
    horaRetiro: '',
    horaDevolucion: ''
  };
  formValid.value = true;
  debouncedSubmit();
};
// Métodos de validación
const validateDates = () => {
  dateErrors.value = {
    fechaRetiro: '',
    fechaDevolucion: '',
    horaRetiro: '',
    horaDevolucion: ''
  };
  formValid.value = true;

  // Validar fecha de retiro
  if (formData.fechaRetiro) {
    const fechaRetiro = new Date(formData.fechaRetiro);
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    if (fechaRetiro < today) {
      dateErrors.value.fechaRetiro = 'La fecha de retiro no puede ser anterior a hoy';
      formValid.value = false;
    }
  }

  // Validar fecha de devolución
  if (formData.fechaRetiro && formData.fechaDevolucion) {
    const fechaRetiro = new Date(formData.fechaRetiro);
    const fechaDevolucion = new Date(formData.fechaDevolucion);

    if (fechaDevolucion < fechaRetiro) {
      dateErrors.value.fechaDevolucion = 'La fecha de devolución debe ser posterior a la fecha de retiro';
      formValid.value = false;
    }
  }
};

const validateTimes = () => {
  if (formData.fechaRetiro && formData.fechaDevolucion &&
    formData.horaRetiro && formData.horaDevolucion &&
    formData.fechaRetiro === formData.fechaDevolucion) {

    const horaRetiro = formData.horaRetiro.split(':').map(Number);
    const horaDevolucion = formData.horaDevolucion.split(':').map(Number);

    const minutosRetiro = horaRetiro[0] * 60 + horaRetiro[1];
    const minutosDevolucion = horaDevolucion[0] * 60 + horaDevolucion[1];

    if (minutosDevolucion <= minutosRetiro) {
      dateErrors.value.horaDevolucion = 'La hora de devolución debe ser posterior a la hora de retiro';
      formValid.value = false;
    } else {
      dateErrors.value.horaDevolucion = '';
    }
  }
};

const validateForm = () => {
  validateDates();
  validateTimes();

  // Ya no requerimos sucursal específica
  if (!formData.fechaRetiro || !formData.horaRetiro) {
    error.value = 'Debe especificar fecha y hora de retiro';
    return false;
  }

  if (!formData.fechaDevolucion || !formData.horaDevolucion) {
    error.value = 'Debe especificar fecha y hora de devolución';
    return false;
  }

  return formValid.value;
};

const handleSubmit = () => {
  error.value = null;

  if (!validateForm()) {
    return;
  }

  const searchData = {
    marca: formData.marca,
    sucursal: formData.sucursal, // Puede ser null
    tipoVehiculo: formData.tipoVehiculo,
    transmision: formData.transmision,
    disponibilidad: formData.estado || 'TODOS',
    fechas: {
      inicio: formData.fechaRetiro
        ? `${formData.fechaRetiro}T${formData.horaRetiro}:00`
        : null,
      fin: formData.fechaDevolucion
        ? `${formData.fechaDevolucion}T${formData.horaDevolucion}:00`
        : null
    },
    precioMinimo: Number(formData.rangoPrecio[0]),
    precioMaximo: Number(formData.rangoPrecio[1]),
    ordenarPor: formData.ordenarPor
  };

  emit('submit', searchData);
  console.log('Submitting search:', searchData);
};
// Métodos
const loadSucursales = async () => {
  loading.value = true;
  error.value = null;
  try {
    const sucursalService = useSucursalService();
    sucursales.value = await sucursalService.listarSucursales();
  } catch (err) {
    error.value = 'Error al cargar sucursales';
    console.error('Error loading sucursales:', err);
  } finally {
    loading.value = false;
  }
};
const debouncedSubmit = debounce(() => {
  handleSubmit();
}, 300);


// Lifecycle hooks
onMounted(async () => {
  await Promise.all([
    loadSucursales(),
    metadataStore.loadMetadata()
  ]);

  // Realizar búsqueda inicial
  handleSubmit();
});
</script>

<style scoped>
.v-text-field >>> .v-input__slot {
  min-height: 40px !important;
}

.search-card {
  background: linear-gradient(to right, #002349, #1E88E5) !important;
  color: white !important;
}

/* Estilos para los inputs dentro de la card */
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

/* Estilo para los textos de label */
.text-subtitle-2 {
  color: rgba(255, 255, 255, 0.9) !important;
}

/* Estilo para el botón */
.v-btn {
  text-transform: none;
  background-color: white !important;
  color: #002349 !important;
}

/* Estilo para el slider de precio */
.search-card :deep(.v-slider-track__fill) {
  background-color: white !important;
}

.search-card :deep(.v-slider-thumb__surface) {
  border-color: white !important;
}

/* Estilo para los campos de texto del rango de precios */
.search-card :deep(.v-text-field input) {
  color: white !important;
}

/* Estilo para la alerta de error */
.v-alert {
  background-color: rgba(244, 67, 54, 0.9) !important;
  color: white !important;
}

/* Ajuste para los selectores */
.search-card :deep(.v-select__selection) {
  color: white !important;
}

.search-card :deep(.v-field__input) {
  color: white !important;
}

/* Estilo para los iconos */
.search-card :deep(.v-icon) {
  color: rgba(255, 255, 255, 0.8) !important;
}

/* Estilo para el texto de los items en los dropdowns */
.search-card :deep(.v-list-item) {
  color: #002349 !important;
}

/* Estilo para el texto de los items en los dropdowns */
.search-card :deep(.v-list-item) {
  color: #002349 !important;
}

/* Estilos para la sección de filtros activos */
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

/* Animaciones para los filtros */
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
