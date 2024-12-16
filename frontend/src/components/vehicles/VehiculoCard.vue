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
      <v-chip
        :color="getStatusColor(vehiculo.estado)"
        size="small"
      >
        {{ vehiculo.estado }}
      </v-chip>
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
        :to="{
          path: '/payment',
          query: {
            vehiculoId: String(vehiculo.id),
            sucursal: String(vehiculo.sucursal?.id),
            precioArriendo: vehiculo.precioArriendo
          }
        }"
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
import {useVehicleStore} from '@/stores/vehicle';

const vehicleStore = useVehicleStore();

const props = defineProps({
  vehiculo: {
    type: Object,
    required: true,
    validator: (obj) => {
      return obj.id && obj.precioArriendo;
    }
  }
});

const emit = defineEmits(['seleccionar']);

const seleccionarVehiculo = () => {
  console.log('Vehículo seleccionado:', props.vehiculo);
  emit('seleccionar', props.vehiculo);
};

const getStatusColor = (estado) => {
  const colors = {
    'DISPONIBLE': 'success',
    'NO_DISPONIBLE': 'error',
    'EN_MANTENCION': 'warning',
    'EN_REPARACION': 'orange'
  };
  return colors[estado] || 'grey';
};

const formatPrice = (price) => {
  return new Intl.NumberFormat('es-CL').format(price);
};

function obtenerImagen(id) {
  return new URL(`../../assets/${id}.png`, import.meta.url).href;
}
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
</style>
