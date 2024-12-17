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
                        <v-list-item-title>Sucursal: {{ sucursal?.nombre }}</v-list-item-title>
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
                          v-model="selectedDates.start"
                          label="Fecha de inicio"
                          type="date"
                          :min="minDate"
                          :rules="dateRules"
                          @update:model-value="calcularTotal"
                        />
                      </v-col>
                      <v-col cols="6">
                        <v-text-field
                          v-model="selectedDates.end"
                          label="Fecha de término"
                          type="date"
                          :min="selectedDates.start || minDate"
                          :rules="dateRules"
                          @update:model-value="calcularTotal"
                        />
                      </v-col>
                    </v-row>
                  </v-card-text>
                </v-card>
              </v-col>

              <!-- Dropdown for return location -->
              <v-col cols="12">
                <v-card
                  flat
                  class="bg-grey-lighten-4"
                >
                  <v-card-text>
                    <h3 class="text-h6 mb-2">
                      Sucursal de Retorno
                    </h3>
                    <v-select
                      v-model="reservation.sucursalRetorno"
                      :items="locations"
                      item-text="nombre"
                      item-value="id"
                      label="Selecciona la sucursal de retorno"
                      :rules="[v => !!v || 'La sucursal de retorno es requerida']"
                    />
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
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { listarSucursales } from '@/services/SucursalService'; // Correctly import the listarSucursales method
import { useVehicleStore } from '@/stores/vehicle';
import { useReservationStore } from '@/stores/reservation';
import { useAuthStore } from '@/stores/auth';
import dayjs from 'dayjs'; // Import dayjs for date formatting

// Define props
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

const reservationStore = useReservationStore();
const authStore = useAuthStore();
const vehicleStore = useVehicleStore();
const router = useRouter();

// Estado
const loading = ref(true);
const procesando = ref(false);
const vehiculo = ref(null);
const sucursal = ref(null);
const locations = ref([]);

const selectedDates = ref({
  start: '',
  end: ''
});

const reservation = ref({
  fechaInicio: '',
  fechaFin: '',
  costo: 0,
  usuario: { id: null },
  vehiculo: { id: null },
  sucursal: { id: null },
  sucursalRetorno: null  // Initialize return location
});

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
  if (!selectedDates.value.start || !selectedDates.value.end) return 0;
  const inicio = new Date(selectedDates.value.start);
  const fin = new Date(selectedDates.value.end);
  return Math.ceil((fin - inicio) / (1000 * 60 * 60 * 24));
});

const totalArriendo = computed(() => {
  return diasArriendo.value * props.precioArriendo;
});

const mostrarTotal = computed(() => {
  return diasArriendo.value > 0;
});

const isFormValid = computed(() => {
  return selectedDates.value.start && selectedDates.value.end && diasArriendo.value > 0 && reservation.value.sucursalRetorno;
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

const calcularTotal = () => {
  if (selectedDates.value.start && selectedDates.value.end) {
    const inicio = new Date(selectedDates.value.start);
    const fin = new Date(selectedDates.value.end);
    if (fin < inicio) {
      selectedDates.value.end = selectedDates.value.start;
    }
  }
};

const cancelarReserva = () => {
  router.push('/');
};

const fetchLocations = async () => {
  try {
    locations.value = await listarSucursales();  // Correctly fetch locations
  } catch (error) {
    console.error('Error fetching locations:', error);
    snackbar.value = {
      show: true,
      color: 'error',
      text: 'Error al cargar las sucursales: ' + (error.message || 'Error desconocido')
    };
  }
};

// Lifecycle hooks
onMounted(async () => {
  try {
    if (!props.vehiculoId || !props.sucursalId) {
      throw new Error('Faltan parámetros requeridos');
    }

    const [vehiculoData, sucursalData] = await Promise.all([
      vehicleStore.getVehicleById(Number(props.vehiculoId)),
      listarSucursales()  // Fetch all sucursales
    ]);

    if (!vehiculoData) {
      throw new Error('No se encontró el vehículo');
    }

    vehiculo.value = vehiculoData;
    sucursal.value = sucursalData.find(sucursal => sucursal.id === Number(props.sucursalId));

    if (!sucursal.value) {
      throw new Error('No se encontró la sucursal');
    }

    locations.value = sucursalData;  // Store the list of sucursales

    await fetchLocations();  // Fetch locations on mount

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
    const reservaData = {
      fechaInicio: dayjs(selectedDates.value.start).format('YYYY-MM-DDTHH:mm:ss'),  // Format date correctly
      fechaFin: dayjs(selectedDates.value.end).format('YYYY-MM-DDTHH:mm:ss'),  // Format date correctly
      costo: totalArriendo.value,
      usuarioId: authStore.user.id,
      vehiculoId: props.vehiculoId,
      sucursalId: props.sucursalId,
      sucursalRetornoId: reservation.value.sucursalRetorno  // Include return location
    };

    await reservationStore.createReservation(reservaData);

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
      text: 'Error al procesar la reserva: ' + error.message
    };
  } finally {
    procesando.value = false;
  }
};
</script>
