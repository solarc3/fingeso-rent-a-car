<template>
  <v-card
    class="mx-auto vehicle-card"
    min-width="450px"
    rounded="l"
    elevation="8"
    ripple
  >
    <v-img
      class="vehiculo-imagen"
      :src="obtenerImagen(vehiculo.id)"
      cover
    />

    <v-card-title class="d-flex justify-space-between align-center">
      <span>{{ vehiculo.marca }} {{ vehiculo.modelo }}</span>
      <v-tooltip
        :text="getAvailabilityTooltip(vehiculo)"
        location="bottom"
      >
        <template #activator="{ props }">
          <v-chip
            v-bind="props"
            :color="getChipColor(vehiculo)"
            size="small"
            class="status-chip"
          >
            {{ getChipText(vehiculo) }}
          </v-chip>
        </template>
      </v-tooltip>
    </v-card-title>

    <v-card-text>
      <v-row dense>
        <v-col cols="6">
          <v-list-item density="compact">
            <template #prepend>
              <v-icon color="primary">
                mdi-car
              </v-icon>
            </template>
            <v-list-item-title>Año: {{ vehiculo.anio }}</v-list-item-title>
          </v-list-item>
        </v-col>

        <v-col cols="6">
          <v-list-item density="compact">
            <template #prepend>
              <v-icon color="primary">
                mdi-license
              </v-icon>
            </template>
            <v-list-item-title>Patente: {{ vehiculo.patente }}</v-list-item-title>
          </v-list-item>
        </v-col>

        <v-col cols="12">
          <v-list-item density="compact">
            <template #prepend>
              <v-icon color="primary">
                mdi-office-building-marker
              </v-icon>
            </template>
            <v-list-item-title>
              Sucursal: {{ vehicleStore.getSucursalName(vehiculo) }}
            </v-list-item-title>
          </v-list-item>
        </v-col>

        <v-col cols="12">
          <v-list-item density="compact">
            <template #prepend>
              <v-icon color="primary">
                mdi-currency-usd
              </v-icon>
            </template>
            <v-list-item-title class="font-weight-bold">
              ${{ formatPrice(vehiculo.precioArriendo) }} / día
            </v-list-item-title>
          </v-list-item>
        </v-col>
      </v-row>
    </v-card-text>

    <v-divider />

    <v-card-actions class="pa-4">
      <v-spacer />
      <v-btn
        color="#be1784"
        class="text-white"
        variant="elevated"
        :disabled="vehiculo.estado !== 'DISPONIBLE'"
        @click="seleccionarVehiculo"
      >
        <v-icon start>
          mdi-car-key
        </v-icon>
        Arrendar
      </v-btn>
    </v-card-actions>
  </v-card>
</template>
<script setup>

import {useRouter} from 'vue-router';
import {useVehicleStore} from '@/stores/vehicle';
import {computed} from 'vue';

const vehicleStore = useVehicleStore();
const router = useRouter();

const props = defineProps({
  vehiculo: {
    type: Object,
    required: true
  },
  fechasSeleccionadas: {
    type: Object,
    default: () => ({
      inicio: null,
      fin: null
    })
  }
});
const getChipColor = (vehiculo) => {
  const tieneReservaActiva = vehiculo.reservas?.some(r =>
    ['CONFIRMADA', 'EN_PROGRESO'].includes(r.estado) &&
    new Date(r.fechaFin) > new Date()
  );

  return tieneReservaActiva ? 'blue' : vehicleStore.getStatusColor(vehiculo.estado);
};


const isAvailable = computed(() => {
  // Primero verificar el estado del vehículo
  if (props.vehiculo.estado !== 'DISPONIBLE') {
    return false;
  }

  // Si no hay fechas seleccionadas, solo considerar el estado
  if (!props.fechasSeleccionadas.inicio || !props.fechasSeleccionadas.fin) {
    return true;
  }

  // Verificar reservas existentes
  return !hasOverlappingReservations.value;
});
const hasOverlappingReservations = computed(() => {
  if (!props.vehiculo.reservas || !Array.isArray(props.vehiculo.reservas)) {
    return false;
  }

  const fechaInicio = new Date(props.fechasSeleccionadas.inicio);
  const fechaFin = new Date(props.fechasSeleccionadas.fin);

  return props.vehiculo.reservas.some(reserva => {
    if (!['CONFIRMADA', 'EN_PROGRESO'].includes(reserva.estado)) {
      return false;
    }

    const reservaInicio = new Date(reserva.fechaInicio);
    const reservaFin = new Date(reserva.fechaFin);

    return fechaInicio < reservaFin && reservaInicio < fechaFin;
  });
});


const emit = defineEmits(['seleccionar']);
const getAvailabilityTooltip = (vehiculo) => {
  if (vehiculo.estado !== 'DISPONIBLE') {
    // Si el estado es ARRENDADO, buscar la reserva activa
    if (vehiculo.estado === 'ARRENDADO') {
      const reservaActiva = vehiculo.reservas?.find(r =>
        r.estado === 'EN_PROGRESO' && new Date(r.fechaFin) > new Date()
      );

      if (reservaActiva) {
        return `Arrendado hasta ${formatDate(reservaActiva.fechaFin)}`;
      }
    }
    return vehicleStore.getStatusText(vehiculo.estado);
  }

  const proximaReserva = vehiculo.reservas
    ?.filter(r => ['CONFIRMADA', 'EN_PROGRESO', 'ARRENDADO'].includes(r.estado))
    .sort((a, b) => new Date(a.fechaInicio) - new Date(b.fechaInicio))
    .find(r => new Date(r.fechaFin) > new Date());

  if (proximaReserva) {
    return `Reservado desde ${formatDate(proximaReserva.fechaInicio)} hasta ${formatDate(proximaReserva.fechaFin)}`;
  }

  return 'Disponible para arrendar';
};
const seleccionarVehiculo = () => {
  if (!isAvailable.value) return;

  router.push({
    name: 'payment',
    query: {
      vehiculoId: props.vehiculo.id,
      sucursal: props.vehiculo.sucursal?.id,
      precioArriendo: props.vehiculo.precioArriendo
    }
  });
};
const formatPrice = (price) => {
  return new Intl.NumberFormat('es-CL').format(price);
};

function obtenerImagen(id) {
  return new URL(`../../assets/${id}.png`, import.meta.url).href;
}

const getChipText = (vehiculo) => {
  // Si está arrendado, mostrar fecha de devolución
  if (vehiculo.estado === 'ARRENDADO') {
    const reservaActiva = vehiculo.reservas
      ?.find(r => r.estado === 'EN_PROGRESO' && new Date(r.fechaFin) > new Date());

    if (reservaActiva) {
      const fechaFin = formatDate(reservaActiva.fechaFin);
      return `Arrendado hasta ${fechaFin}`;
    }
  }

  // Para otros estados, mostrar la próxima reserva
  const reservaActiva = vehiculo.reservas
    ?.filter(r => ['CONFIRMADA', 'EN_PROGRESO'].includes(r.estado))
    .sort((a, b) => new Date(a.fechaInicio) - new Date(b.fechaInicio))
    .find(r => new Date(r.fechaFin) > new Date());

  if (reservaActiva) {
    const fechaFin = formatDate(reservaActiva.fechaFin);
    return `Reservado hasta ${fechaFin}`;
  }

  return vehicleStore.getStatusText(vehiculo.estado);
};

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('es-CL', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  });
};
</script>

<style scoped>
.vehicle-card {
  transition: transform 0.2s;
}

.vehicle-card:hover {
  transform: translateY(-5px);
}

.vehiculo-imagen {
  width: 600px;
  height: 300px;
  object-fit: cover;
}

.v-list-item {
  min-height: 40px !important;
}

.v-list-item__title {
  font-size: 0.9rem !important;
}

.v-card-title {
  font-size: 1.25rem !important;
  padding: 16px 16px 8px 16px;
}

.v-card-text {
  padding: 8px 16px;
}

.v-card-actions {
  padding: 8px 16px;
}

.v-chip {
  font-size: 0.75rem !important;
}

.v-icon {
  margin-right: 8px;
}

/* Responsive adjustments */
@media (max-width: 600px) {
  .vehiculo-imagen {
    height: 200px;
  }

  .v-card-title {
    font-size: 1.1rem !important;
  }

  .v-list-item__title {
    font-size: 0.8rem !important;
  }
}

/* Animation for status chip */
.v-chip {
  transition: all 0.3s ease;
}

.v-chip:hover {
  transform: scale(1.05);
}

/* Button styling */
.v-btn {
  text-transform: none;
  font-weight: 500;
  letter-spacing: 0.5px;
}

.v-btn:not(:disabled) {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.v-btn:disabled {
  opacity: 0.7;
}

/* Price emphasis */
.font-weight-bold {
  color: #be1784;
  font-size: 1.1rem !important;
}

.status-chip {
  transition: transform 0.2s ease;
}

.status-chip:hover {
  transform: scale(1.05);
}
</style>
