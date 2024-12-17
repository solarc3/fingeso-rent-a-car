<template>
  <v-card>
    <v-card-title class="headline mb-2 d-flex align-center">
      <span>Historial del Vehículo</span>
      <v-spacer />
      <v-btn
        icon="mdi-refresh"
        size="small"
        :loading="loading"
        @click="loadHistory"
      />
    </v-card-title>

    <v-card-text
      v-if="loading"
      class="text-center"
    >
      <v-progress-circular
        indeterminate
        color="primary"
      />
    </v-card-text>

    <v-card-text
      v-else-if="!historial.length"
      class="text-center"
    >
      <v-icon
        size="64"
        color="grey"
      >
        mdi-history
      </v-icon>
      <p class="text-subtitle-1 mt-2">
        No hay registros en el historial
      </p>
    </v-card-text>

    <v-timeline
      v-else
      density="compact"
    >
      <v-timeline-item
        v-for="evento in ordenarHistorial"
        :key="evento.id"
        :dot-color="getEventColor(evento.tipoEvento)"
        size="small"
      >
        <template #opposite>
          {{ formatDate(evento.fecha) }}
        </template>

        <v-card variant="outlined">
          <v-card-title class="text-subtitle-1 pb-1">
            {{ getEventTitle(evento.tipoEvento) }}
          </v-card-title>

          <v-card-text>
            <div class="d-flex flex-column">
              <p class="mb-2">
                {{ evento.descripcion }}
              </p>
              <div class="d-flex align-center gap-2">
                <v-chip
                  :color="getStatusColor(evento.estadoAnterior)"
                  size="small"
                >
                  {{ evento.estadoAnterior }}
                </v-chip>
                <v-icon>mdi-arrow-right</v-icon>
                <v-chip
                  :color="getStatusColor(evento.estadoNuevo)"
                  size="small"
                >
                  {{ evento.estadoNuevo }}
                </v-chip>
              </div>
              <div
                v-if="evento.registradoPor"
                class="mt-2 text-caption text-grey"
              >
                Registrado por: {{ evento.registradoPor.nombre }} {{ evento.registradoPor.apellido }}
              </div>
            </div>
          </v-card-text>
        </v-card>
      </v-timeline-item>
    </v-timeline>
  </v-card>
</template>
<script setup>
import {ref, computed, onMounted, watch} from 'vue';
import {useMantenimientoService} from '@/services/MantenimientoService';
import {useVehicleStore} from '@/stores/vehicle';

const props = defineProps({
  vehiculoId: {
    type: Number,
    required: true
  },
  shouldRefresh: {
    type: Boolean,
    default: false
  }
});

const historial = ref([]);
const loading = ref(false);
const vehicleStore = useVehicleStore();

// Computed property para ordenar el historial
const ordenarHistorial = computed(() => {
  if (!Array.isArray(historial.value)) {
    console.warn('historial.value no es un array:', historial.value);
    return [];
  }

  return [...historial.value].sort((a, b) => {
    const fechaA = new Date(a.fecha).getTime();
    const fechaB = new Date(b.fecha).getTime();
    return fechaB - fechaA;
  });
});

const getEventColor = (tipo) => {
  const colors = {
    'MANTENIMIENTO_PROGRAMADO': 'warning',
    'ACTUALIZACION_MANTENIMIENTO': 'info',
    'CAMBIO_ESTADO': 'primary',
    'REPARACION': 'error'
  };
  return colors[tipo] || 'grey';
};

const getEventTitle = (tipo) => {
  const titles = {
    'MANTENIMIENTO_PROGRAMADO': 'Mantenimiento Programado',
    'ACTUALIZACION_MANTENIMIENTO': 'Actualización de Mantenimiento',
    'CAMBIO_ESTADO': 'Cambio de Estado',
    'REPARACION': 'Reparación'
  };
  return titles[tipo] || tipo;
};

const getStatusColor = (estado) => {
  return vehicleStore.getStatusColor(estado);
};

const formatDate = (date) => {
  return new Date(date).toLocaleString('es-CL', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const loadHistory = async () => {
  if (loading.value) return;

  loading.value = true;
  try {
    const mantenimientoService = useMantenimientoService();
    const response = await mantenimientoService.obtenerHistorial(props.vehiculoId);

    if (Array.isArray(response)) {
      historial.value = response;
    } else if (response.eventos && Array.isArray(response.eventos)) {
      historial.value = response.eventos;
    } else {
      console.error('Formato de respuesta inesperado:', response);
      historial.value = [];
    }

    console.log('Historial actualizado:', {
      cantidad: historial.value.length,
      eventos: historial.value
    });
  } catch (error) {
    console.error('Error cargando historial:', error);
    historial.value = [];
  } finally {
    loading.value = false;
  }
};

// Watchers
watch(() => props.vehiculoId, (newId) => {
  if (newId) {
    loadHistory();
  }
});

watch(() => props.shouldRefresh, (shouldRefresh) => {
  if (shouldRefresh) {
    loadHistory();
  }
});

// Lifecycle hooks
onMounted(() => {
  loadHistory();
});
</script>
<style scoped>
.v-timeline {
  padding: 16px;
}

.v-card {
  transition: transform 0.2s;
}

.v-card:hover {
  transform: translateY(-2px);
}

.v-timeline-item {
  margin-bottom: 8px;
}

.text-caption {
  font-size: 0.75rem;
}
</style>
