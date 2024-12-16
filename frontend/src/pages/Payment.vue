<template>
  <v-container fluid>
    <v-row justify="center">
      <v-col
        cols="12"
        md="8"
      >
        <v-card
          class="pa-6 mb-4"
          rounded="l"
        >
          <v-card-title class="text-h5 mb-4">
            Detalles del Arriendo
          </v-card-title>

          <!-- Loading state -->
          <div
            v-if="loading"
            class="d-flex justify-center align-center py-4"
          >
            <v-progress-circular
              indeterminate
              color="primary"
            />
          </div>

          <template v-else>
            <!-- Detalles del vehículo -->
            <v-row>
              <v-col
                cols="12"
                md="6"
              >
                <v-card
                  flat
                  class="bg-grey-lighten-4"
                >
                  <v-card-text>
                    <h3 class="text-h6 mb-2">
                      Vehículo Seleccionado
                    </h3>
                    <v-list>
                      <v-list-item>
                        <template #prepend>
                          <v-icon
                            color="primary"
                            class="mr-2"
                          >
                            mdi-car
                          </v-icon>
                        </template>
                        <v-list-item-title>{{ vehiculo?.marca }} {{ vehiculo?.modelo }}</v-list-item-title>
                      </v-list-item>

                      <v-list-item>
                        <template #prepend>
                          <v-icon
                            color="primary"
                            class="mr-2"
                          >
                            mdi-currency-usd
                          </v-icon>
                        </template>
                        <v-list-item-title>${{ precioArriendo }} por día</v-list-item-title>
                      </v-list-item>

                      <v-list-item>
                        <template #prepend>
                          <v-icon
                            color="primary"
                            class="mr-2"
                          >
                            mdi-office-building-marker
                          </v-icon>
                        </template>
                        <v-list-item-title>
                          Sucursal: {{ vehicleStore.getSucursalName(vehiculo) }}
                        </v-list-item-title>
                      </v-list-item>
                    </v-list>
                  </v-card-text>
                </v-card>
              </v-col>

              <!-- Selector de fechas -->
              <v-col
                cols="12"
                md="6"
              >
                <v-card
                  flat
                  class="bg-grey-lighten-4"
                >
                  <v-card-text>
                    <h3 class="text-h6 mb-2">
                      Fechas del Arriendo
                    </h3>
                    <v-row>
                      <v-col cols="6">
                        <v-text-field
                          v-model="fechaInicio"
                          label="Fecha de inicio"
                          type="date"
                          :min="minDate"
                          :rules="dateRules"
                          @update:model-value="calcularTotal"
                        />
                      </v-col>
                      <v-col cols="6">
                        <v-text-field
                          v-model="fechaFin"
                          label="Fecha de término"
                          type="date"
                          :min="fechaInicio || minDate"
                          :rules="dateRules"
                          @update:model-value="calcularTotal"
                        />
                      </v-col>
                    </v-row>
                  </v-card-text>
                </v-card>
              </v-col>
            </v-row>

            <!-- Total y costos -->
            <v-card
              v-if="mostrarTotal"
              class="mt-4 bg-grey-lighten-4"
              flat
            >
              <v-card-text>
                <div class="d-flex justify-space-between align-center">
                  <div>
                    <div class="text-subtitle-1">
                      Días de arriendo: {{ diasArriendo }}
                    </div>
                    <div class="text-subtitle-1">
                      Precio por día: ${{ precioArriendo }}
                    </div>
                  </div>
                  <div class="text-h5">
                    Total: ${{ totalArriendo }}
                  </div>
                </div>
              </v-card-text>
            </v-card>

            <!-- Botones de acción -->
            <v-card-actions class="mt-4">
              <v-spacer />
              <v-btn
                color="error"
                variant="text"
                @click="cancelarReserva"
              >
                Cancelar
              </v-btn>
              <v-btn
                color="primary"
                :disabled="!isFormValid"
                :loading="procesando"
                @click="confirmarReserva"
              >
                Confirmar Reserva
              </v-btn>
            </v-card-actions>
          </template>
        </v-card>
      </v-col>
    </v-row>

    <!-- Snackbar para mensajes -->
    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      :timeout="3000"
    >
      {{ snackbar.text }}

      <template #actions>
        <v-btn
          color="white"
          text
          @click="snackbar.show = false"
        >
          Cerrar
        </v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script setup>
import {ref, computed, onMounted} from 'vue';
import {useRouter} from 'vue-router';
import {useSucursalService} from '@/services/SucursalService';
import {useAuthStore} from '@/stores/auth';
import {useVehiculoService} from "@/services/VehiculoService.js";

import {useVehicleStore} from '@/stores/vehicle';

const vehicleStore = useVehicleStore();
const router = useRouter();
const authStore = useAuthStore();

// Estado
const loading = ref(true);
const procesando = ref(false);
const fechaInicio = ref('');
const fechaFin = ref('');
const vehiculo = ref(null);
const sucursal = ref(null);

const snackbar = ref({
  show: false,
  text: '',
  color: 'success'
});

// Computed properties
const minDate = computed(() => {
  const today = new Date();
  return today.toISOString().split('T')[0];
});

const diasArriendo = computed(() => {
  if (!fechaInicio.value || !fechaFin.value) return 0;
  const inicio = new Date(fechaInicio.value);
  const fin = new Date(fechaFin.value);
  return Math.ceil((fin - inicio) / (1000 * 60 * 60 * 24));
});

const totalArriendo = computed(() => {
  return diasArriendo.value * props.precioArriendo;
});

const mostrarTotal = computed(() => {
  return diasArriendo.value > 0;
});

const isFormValid = computed(() => {
  return fechaInicio.value && fechaFin.value && diasArriendo.value > 0;
});

// Reglas de validación
const dateRules = [
  v => !!v || 'La fecha es requerida',
  v => {
    const date = new Date(v);
    const today = new Date();
    return date >= today || 'La fecha debe ser futura';
  }
];

// Métodos
const calcularTotal = () => {
  if (fechaInicio.value && fechaFin.value) {
    const inicio = new Date(fechaInicio.value);
    const fin = new Date(fechaFin.value);
    if (fin < inicio) {
      fechaFin.value = fechaInicio.value;
    }
  }
};

const cancelarReserva = () => {
  router.push('/');
};

const confirmarReserva = async () => {
  if (!authStore.isAuthenticated) {
    snackbar.value = {
      show: true,
      color: 'warning',
      text: 'Debe iniciar sesión para realizar una reserva'
    };
    return;
  }

  procesando.value = true;
  try {
    // Aquí iría la lógica para crear la reserva
    snackbar.value = {
      show: true,
      color: 'success',
      text: 'Reserva confirmada exitosamente'
    };
    setTimeout(() => {
      router.push('/mis-reservas');
    }, 1500);
  } catch (error) {
    snackbar.value = {
      show: true,
      color: 'error',
      text: 'Error al procesar la reserva'
    };
  } finally {
    procesando.value = false;
  }
};

// Lifecycle hooks
onMounted(async () => {
  try {
    if (!props.vehiculoId || !props.sucursalId) {
      throw new Error('Faltan parámetros requeridos');
    }

    const vehiculoService = useVehiculoService();
    const sucursalService = useSucursalService();

    const [vehiculoData, sucursalData] = await Promise.all([
      vehicleStore.getVehicleById(Number(props.vehiculoId)),
      sucursalService.obtenerSucursalPorId(Number(props.sucursalId))
    ]);

    if (!vehiculoData) {
      throw new Error('No se encontró el vehículo');
    }

    if (!sucursalData) {
      throw new Error('No se encontró la sucursal');
    }

    vehiculo.value = vehiculoData;
    sucursal.value = sucursalData;

  } catch (error) {
    console.error('Error cargando datos:', error);
    snackbar.value = {
      show: true,
      color: 'error',
      text: 'Error al cargar los datos: ' + (error.message || 'Error desconocido')
    };
    setTimeout(() => router.push('/'), 3000);
  } finally {
    loading.value = false;
  }
});
const props = defineProps({
  vehiculoId: {
    type: [String, Number],
    required: true,
    validator: (value) => !isNaN(Number(value))
  },
  sucursalId: {
    type: [String, Number],
    required: true,
    validator: (value) => !isNaN(Number(value))
  },
  precioArriendo: {
    type: Number,
    required: true
  }
});

// Modificar el onMounted para mejor manejo de errores
onMounted(async () => {
  try {
    const vehiculoService = useVehiculoService();
    const sucursalService = useSucursalService();

    // Cargar datos en paralelo
    const [vehiculoData, sucursalData] = await Promise.all([
      vehicleStore.getVehicleById(Number(props.vehiculoId)),
      sucursalService.obtenerSucursalPorId(Number(props.sucursalId))
    ]);

    vehiculo.value = vehiculoData;
    sucursal.value = sucursalData;

    if (!vehiculo.value || !sucursal.value) {
      throw new Error('No se encontraron los datos del vehículo o sucursal');
    }

  } catch (error) {
    console.error('Error cargando datos:', error);
    snackbar.value = {
      show: true,
      color: 'error',
      text: 'Error al cargar los datos: ' + (error.message || 'Error desconocido')
    };
    // Opcional: Redirigir al home después de un error
    setTimeout(() => router.push('/'), 3000);
  } finally {
    loading.value = false;
  }
});
const loadData = async () => {
  try {
    loading.value = true;

    // Convertir IDs a números
    const vehiculoId = Number(props.vehiculoId);
    const sucursalId = Number(props.sucursalId);

    if (!vehiculoId || !sucursalId) {
      throw new Error('IDs inválidos');
    }

    // Cargar datos en paralelo
    const [vehiculoData, sucursalData] = await Promise.all([
      vehicleStore.getVehicleById(vehiculoId),
      useSucursalService().obtenerSucursalPorId(sucursalId)
    ]);

    // Validar datos
    if (!vehiculoData) {
      throw new Error('No se encontró el vehículo');
    }

    if (!sucursalData) {
      throw new Error('No se encontró la sucursal');
    }

    // Asignar datos
    vehiculo.value = vehiculoData;
    sucursal.value = sucursalData;

    console.log('Datos cargados:', {
      vehiculo: vehiculo.value,
      sucursal: sucursal.value
    });

  } catch (error) {
    console.error('Error cargando datos:', error);
    snackbar.value = {
      show: true,
      color: 'error',
      text: 'Error al cargar los datos: ' + (error.message || 'Error desconocido')
    };
    // Redireccionar después de un error
    setTimeout(() => router.push('/'), 3000);
  } finally {
    loading.value = false;
  }
};
onMounted(() => {
  loadData();
});
</script>
