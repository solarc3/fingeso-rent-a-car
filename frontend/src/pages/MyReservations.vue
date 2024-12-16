<template>
  <v-container fluid>
    <!-- Loading state -->
    <div
      v-if="loading"
      class="d-flex justify-center pa-4"
    >
      <v-progress-circular
        indeterminate
        color="primary"
      />
    </div>

    <!-- Sin reservas -->
    <v-card
      v-else-if="!reservas.length"
      class="text-center pa-4"
    >
      <v-icon
        size="64"
        color="grey"
      >
        mdi-calendar-blank
      </v-icon>
      <p class="text-h6 mt-2">
        No tienes reservas activas
      </p>
      <v-btn
        color="primary"
        to="/"
        class="mt-4"
      >
        Buscar vehículos
      </v-btn>
    </v-card>

    <!-- Vista de reservas por estado -->
    <template v-else>
      <!-- Tabs para filtrar por estado -->
      <v-tabs
        v-model="activeTab"
        class="mb-4"
      >
        <v-tab value="all">
          Todas
        </v-tab>
        <v-tab value="active">
          Activas
        </v-tab>
        <v-tab value="completed">
          Completadas
        </v-tab>
      </v-tabs>

      <v-row>
        <v-col
          v-for="reserva in reservasFiltradas"
          :key="reserva.id"
          cols="12"
          sm="6"
          md="4"
        >
          <v-card
            :class="['reservation-card', `border-${getStatusColor(reserva.estado)}`]"
            elevation="2"
          >
            <!-- Header -->
            <v-card-item>
              <template #prepend>
                <v-icon
                  :color="getStatusColor(reserva.estado)"
                  size="large"
                >
                  {{ getStatusIcon(reserva.estado) }}
                </v-icon>
              </template>
              <v-card-title>
                {{ reserva.vehiculo.marca }} {{ reserva.vehiculo.modelo }}
              </v-card-title>
              <v-card-subtitle>
                {{ reserva.sucursal.nombre }}
              </v-card-subtitle>
            </v-card-item>

            <v-divider />

            <!-- Detalles -->
            <v-card-text>
              <v-list density="compact">
                <v-list-item>
                  <template #prepend>
                    <v-icon color="primary">
                      mdi-calendar-start
                    </v-icon>
                  </template>
                  <v-list-item-title>
                    Desde: {{ formatDate(reserva.fechaInicio) }}
                  </v-list-item-title>
                </v-list-item>

                <v-list-item>
                  <template #prepend>
                    <v-icon color="primary">
                      mdi-calendar-end
                    </v-icon>
                  </template>
                  <v-list-item-title>
                    Hasta: {{ formatDate(reserva.fechaFin) }}
                  </v-list-item-title>
                </v-list-item>

                <v-list-item>
                  <template #prepend>
                    <v-icon color="primary">
                      mdi-cash
                    </v-icon>
                  </template>
                  <v-list-item-title class="font-weight-bold">
                    ${{ formatPrice(reserva.costo) }}
                  </v-list-item-title>
                </v-list-item>
              </v-list>
            </v-card-text>

            <!-- Acciones -->
            <v-card-actions>
              <v-chip
                :color="getStatusColor(reserva.estado)"
                size="small"
                class="ml-2"
              >
                {{ getStatusText(reserva.estado) }}
              </v-chip>
              <v-spacer />
              <template v-if="reserva.estado === 'PENDIENTE'">
                <v-btn
                  color="success"
                  :loading="processingPayment === reserva.id"
                  @click="procesarPago(reserva)"
                >
                  <v-icon start>
                    mdi-credit-card
                  </v-icon>
                  Pagar
                </v-btn>
              </template>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
    </template>
  </v-container>
</template>
<script setup>
import {ref, computed, onMounted} from 'vue';
import {useReservationStore} from '@/stores/reservation';
import {useAuthStore} from '@/stores/auth';

const reservationStore = useReservationStore();
const authStore = useAuthStore();

// Estado
const loading = computed(() => reservationStore.loading);
const reservas = computed(() => reservationStore.reservations);
const activeTab = ref('all');
const processingPayment = ref(null);

// Computed

const reservasFiltradas = computed(() => {
  let resultado = [...reservas.value];
  switch (activeTab.value) {
    case 'active':
      return resultado.filter(r =>
        ['PENDIENTE', 'CONFIRMADA', 'EN_PROGRESO'].includes(r.estado)
      );
    case 'completed':
      return resultado.filter(r =>
        ['COMPLETADA', 'CANCELADA'].includes(r.estado)
      );
    default:
      return resultado;
  }
});

// Métodos de utilidad
const formatDate = (date) => {
  return new Date(date).toLocaleString('es-CL', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const formatPrice = (price) => {
  return new Intl.NumberFormat('es-CL').format(price);
};

const getStatusColor = (estado) => {
  const colors = {
    'PENDIENTE': 'warning',
    'CONFIRMADA': 'success',
    'EN_PROGRESO': 'info',
    'COMPLETADA': 'grey',
    'CANCELADA': 'error'
  };
  return colors[estado] || 'grey';
};

const getStatusIcon = (estado) => {
  const icons = {
    'PENDIENTE': 'mdi-clock-outline',
    'CONFIRMADA': 'mdi-check-circle',
    'EN_PROGRESO': 'mdi-car',
    'COMPLETADA': 'mdi-flag-checkered',
    'CANCELADA': 'mdi-cancel'
  };
  return icons[estado] || 'mdi-help-circle';
};

const getStatusText = (estado) => {
  return estado.replace('_', ' ');
};

const procesarPago = async (reserva) => {
  processingPayment.value = reserva.id;
  try {
    await reservationStore.updateReservationStatus(reserva.id, 'CONFIRMADA');
  } catch (error) {
    console.error('Error procesando pago:', error);
  } finally {
    processingPayment.value = null;
  }
};

// Lifecycle hooks
onMounted(async () => {
  if (authStore.user?.id) {
    await reservationStore.fetchUserReservations(authStore.user.id);
  }
});
</script>

<style scoped>
.reservation-card {
  transition: transform 0.2s;
  border-left: 4px solid;
}

.reservation-card:hover {
  transform: translateY(-4px);
}

.border-warning {
  border-left-color: var(--v-warning-base);
}

.border-success {
  border-left-color: var(--v-success-base);
}

.border-info {
  border-left-color: var(--v-info-base);
}

.border-grey {
  border-left-color: var(--v-grey-base);
}

.border-error {
  border-left-color: var(--v-error-base);
}

.v-list-item {
  min-height: 40px;
}
</style>
