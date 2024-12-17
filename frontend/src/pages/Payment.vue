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
                      item-title="nombre"
                      item-value="id"
                      label="Selecciona la sucursal de retorno"
                      :rules="[v => !!v || 'La sucursal de retorno es requerida']"
                      return-object
                    >
                      <template #item="{ props, item }">
                        <v-list-item v-bind="props">
                          <v-list-item-subtitle>{{ item.raw.direccion }}</v-list-item-subtitle>
                        </v-list-item>
                      </template>
                    </v-select>
                  </v-card-text>
                </v-card>
              </v-col>
            </v-row>
            <v-col cols="12">
              <v-card
                flat
                class="bg-grey-lighten-4"
              >
                <v-card-title class="text-subtitle-1">
                  Fechas No Disponibles
                </v-card-title>
                <v-card-text>
                  <v-list
                    v-if="reservasExistentes.length > 0"
                    density="compact"
                  >
                    <v-list-item
                      v-for="(reserva, index) in reservasExistentes"
                      :key="index"
                    >
                      <template #prepend>
                        <v-icon color="warning">
                          mdi-calendar-lock
                        </v-icon>
                      </template>
                      <v-list-item-title>
                        {{ formatDate(reserva.fechaInicio) }} - {{ formatDate(reserva.fechaFin) }}
                      </v-list-item-title>
                    </v-list-item>
                  </v-list>
                  <v-alert
                    v-else
                    type="info"
                    variant="tonal"
                    density="compact"
                  >
                    No hay reservas programadas para este vehículo
                  </v-alert>
                </v-card-text>
              </v-card>
            </v-col>
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
import {useVehicleStore} from '@/stores/vehicle';
import {useReservationStore} from '@/stores/reservation';
import {useAuthStore} from '@/stores/auth';
import dayjs from 'dayjs';

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
  usuario: {id: null},
  vehiculo: {id: null},
  sucursal: {id: null},
  sucursalRetorno: null
});

const snackbar = ref({
  show: false,
  text: '',
  color: 'success'
});

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
    const sucursalService = useSucursalService();
    const sucursales = await sucursalService.listarSucursales();
    // Asegúrate de que cada sucursal tenga las propiedades necesarias
    locations.value = sucursales.map(sucursal => ({
      id: sucursal.id,
      nombre: sucursal.nombre,
      direccion: sucursal.direccion
    }));
  } catch (error) {
    console.error('Error fetching locations:', error);
    snackbar.value = {
      show: true,
      color: 'error',
      text: 'Error al cargar las sucursales: ' + (error.message || 'Error desconocido')
    };
  }
};

onMounted(async () => {
  try {
    if (!props.vehiculoId || !props.sucursalId) {
      throw new Error('Faltan parámetros requeridos');
    }

    const sucursalService = useSucursalService();
    const [vehiculoData, sucursalData] = await Promise.all([
      vehicleStore.getVehicleById(Number(props.vehiculoId)),
      sucursalService.listarSucursales()
    ]);

    if (!vehiculoData) {
      throw new Error('No se encontró el vehículo');
    }

    vehiculo.value = vehiculoData;
    sucursal.value = sucursalData.find(sucursal => sucursal.id === Number(props.sucursalId));

    if (!sucursal.value) {
      throw new Error('No se encontró la sucursal');
    }

    locations.value = sucursalData;

    await fetchLocations();

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

const confirmarReserva = async () =>
  {
    if (!authStore.isAuthenticated) {
      snackbar.value = {
        show: true,
        color: 'warning',
        text: 'Debe iniciar sesión para realizar una reserva'
      };
      return;
    }

    if (!selectedDates.value.start ||
      !selectedDates.value.end ||
      !reservation.value.sucursalRetorno?.id ||
      !props.vehiculoId ||
      !props.sucursalId ||
      !authStore.user.id) {
      snackbar.value = {
        show: true,
        color: 'error',
        text: 'Por favor complete todos los campos requeridos'
      };
      return;
    }

    procesando.value = true;
    try {
      const reservaData = {
        fechaInicio: dayjs(selectedDates.value.start).format('YYYY-MM-DDTHH:mm:ss'),
        fechaFin: dayjs(selectedDates.value.end).format('YYYY-MM-DDTHH:mm:ss'),
        costo: totalArriendo.value,
        usuarioId: authStore.user.id,
        vehiculoId: Number(props.vehiculoId),
        sucursalId: Number(props.sucursalId),
        sucursalDevolucionId: Number(reservation.value.sucursalRetorno.id)
      };

      console.log('Datos de la reserva a enviar:', reservaData);

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
      console.error('Error completo:', error);
      snackbar.value = {
        show: true,
        color: 'error',
        text: 'Error al procesar la reserva: ' + error.message
      };
    } finally {
      procesando.value = false;
    }
  }
;
const reservasExistentes = computed(() => {
  if (!vehiculo.value?.reservas || !Array.isArray(vehiculo.value.reservas)) {
    return [];
  }

  const ahora = new Date();
  return vehiculo.value.reservas
    .filter(reserva =>
      ['CONFIRMADA', 'EN_PROGRESO'].includes(reserva.estado) &&
      new Date(reserva.fechaFin) > ahora
    )
    .sort((a, b) => new Date(a.fechaInicio) - new Date(b.fechaInicio));
});


const formatDate = (date) => {
  return new Date(date).toLocaleDateString('es-CL', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};
</script>
