<template>
  <v-card>
    <v-card-title class="d-flex justify-space-between align-center">
      <span>Historial de Fallas</span>
      <v-btn
        icon="mdi-refresh"
        size="small"
        :loading="loading"
        @click="loadFallas"
      />
    </v-card-title>

    <v-card-text>
      <v-data-table
        v-if="fallas.length > 0"
        :headers="headers"
        :items="fallas"
        :loading="loading"
        density="comfortable"
      >
        <template #item.fechaReporte="{ item }">
          {{ formatDate(item.fechaReporte) }}
        </template>

        <template #item.tipo="{ item }">
          <v-chip
            :color="getTipoColor(item.tipo)"
            size="small"
          >
            {{ item.tipo }}
          </v-chip>
        </template>

        <template #item.severidad="{ item }">
          <v-chip
            :color="getSeveridadColor(item.severidad)"
            size="small"
          >
            {{ item.severidad }}
          </v-chip>
        </template>

        <template #item.estado="{ item }">
          <v-chip
            :color="getEstadoColor(item.estado)"
            size="small"
          >
            {{ item.estado }}
          </v-chip>
        </template>

        <template #item.actions="{ item }">
          <v-tooltip text="Ver detalles">
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                icon="mdi-information"
                size="small"
                color="info"
                class="mr-2"
                @click="openDetailsDialog(item)"
              />
            </template>
          </v-tooltip>

          <v-tooltip
            v-if="canResolve(item)"
            text="Resolver falla"
          >
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                icon="mdi-check-circle"
                size="small"
                color="success"
                @click="openResolveDialog(item)"
              />
            </template>
          </v-tooltip>
        </template>
      </v-data-table>

      <v-alert
        v-else
        type="info"
        variant="tonal"
        class="mt-4"
      >
        No hay fallas registradas para este vehículo
      </v-alert>
    </v-card-text>

    <!-- Diálogo de detalles -->
    <v-dialog
      v-model="dialogs.details"
      max-width="600"
    >
      <v-card v-if="selectedFalla">
        <v-card-title>Detalles de la Falla</v-card-title>
        <v-card-text>
          <v-list>
            <v-list-item>
              <v-list-item-title class="font-weight-bold">
                Fecha de Reporte
              </v-list-item-title>
              <v-list-item-subtitle>
                {{ formatDate(selectedFalla.fechaReporte) }}
              </v-list-item-subtitle>
            </v-list-item>

            <v-list-item>
              <v-list-item-title class="font-weight-bold">
                Tipo
              </v-list-item-title>
              <v-list-item-subtitle>
                <v-chip
                  :color="getTipoColor(selectedFalla.tipo)"
                  size="small"
                >
                  {{ selectedFalla.tipo }}
                </v-chip>
              </v-list-item-subtitle>
            </v-list-item>

            <v-list-item>
              <v-list-item-title class="font-weight-bold">
                Severidad
              </v-list-item-title>
              <v-list-item-subtitle>
                <v-chip
                  :color="getSeveridadColor(selectedFalla.severidad)"
                  size="small"
                >
                  {{ selectedFalla.severidad }}
                </v-chip>
              </v-list-item-subtitle>
            </v-list-item>

            <v-list-item>
              <v-list-item-title class="font-weight-bold">
                Descripción
              </v-list-item-title>
              <v-list-item-subtitle>
                {{ selectedFalla.descripcion }}
              </v-list-item-subtitle>
            </v-list-item>

            <v-list-item v-if="selectedFalla.reportadoPor">
              <v-list-item-title class="font-weight-bold">
                Reportado por
              </v-list-item-title>
              <v-list-item-subtitle>
                {{ selectedFalla.reportadoPor.nombre }} {{ selectedFalla.reportadoPor.apellido }}
              </v-list-item-subtitle>
            </v-list-item>

            <template v-if="selectedFalla.solucion">
              <v-divider class="my-3" />
              <v-list-item>
                <v-list-item-title class="font-weight-bold">
                  Solución
                </v-list-item-title>
                <v-list-item-subtitle>
                  {{ selectedFalla.solucion }}
                </v-list-item-subtitle>
              </v-list-item>

              <v-list-item v-if="selectedFalla.resueltoPor">
                <v-list-item-title class="font-weight-bold">
                  Resuelto por
                </v-list-item-title>
                <v-list-item-subtitle>
                  {{ selectedFalla.resueltoPor.nombre }} {{ selectedFalla.resueltoPor.apellido }}
                </v-list-item-subtitle>
              </v-list-item>
            </template>
          </v-list>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            color="primary"
            @click="dialogs.details = false"
          >
            Cerrar
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Diálogo para resolver falla -->
    <v-dialog
      v-model="dialogs.resolve"
      max-width="500"
    >
      <v-card>
        <v-card-title>Resolver Falla</v-card-title>
        <v-card-text>
          <v-form
            ref="resolveForm"
            v-model="formValid"
          >
            <v-textarea
              v-model="resolucionData.solucion"
              label="Solución aplicada"
              :rules="rules.required"
              rows="4"
            />
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            color="grey"
            variant="text"
            @click="closeResolveDialog"
          >
            Cancelar
          </v-btn>
          <v-btn
            color="success"
            :loading="saving"
            :disabled="!formValid"
            @click="resolverFalla"
          >
            Confirmar
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Snackbar -->
    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      :timeout="3000"
    >
      {{ snackbar.text }}
      <template #actions>
        <v-btn
          color="white"
          variant="text"
          @click="snackbar.show = false"
        >
          Cerrar
        </v-btn>
      </template>
    </v-snackbar>
  </v-card>
</template>

<script setup>
import {ref, onMounted} from 'vue';
import {useMantenimientoService} from '@/services/MantenimientoService';
import {useAuthStore} from '@/stores/auth';

const authStore = useAuthStore();

const props = defineProps({
  vehiculoId: {
    type: Number,
    required: true
  }
});

const emit = defineEmits(['falla-resuelta']);

// Estado
const loading = ref(false);
const saving = ref(false);
const formValid = ref(false);
const fallas = ref([]);
const selectedFalla = ref(null);
const resolveForm = ref(null);

// Diálogos y snackbar
const dialogs = ref({
  details: false,
  resolve: false
});

const snackbar = ref({
  show: false,
  text: '',
  color: 'success'
});

// Datos del formulario
const resolucionData = ref({
  solucion: ''
});

// Configuración de la tabla
const headers = [
  {title: 'Fecha', key: 'fechaReporte', sortable: true},
  {title: 'Tipo', key: 'tipo', sortable: true},
  {title: 'Severidad', key: 'severidad', sortable: true},
  {title: 'Descripción', key: 'descripcion'},
  {title: 'Estado', key: 'estado', sortable: true},
  {title: 'Acciones', key: 'actions', sortable: false}
];

// Reglas de validación
const rules = {
  required: [v => !!v || 'Este campo es requerido']
};

// Métodos
const loadFallas = async () => {
  if (loading.value) return;

  loading.value = true;
  try {
    const mantenimientoService = useMantenimientoService();
    // Change this line from obtenerHistorialFallas to obtenerHistorial
    fallas.value = await mantenimientoService.obtenerHistorial(props.vehiculoId);
  } catch (error) {
    console.error('Error cargando fallas:', error);
    historial.value = [];
  } finally {
    loading.value = false;
  }
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

const getTipoColor = (tipo) => {
  const colors = {
    'MECANICA': 'orange',
    'ELECTRICA': 'blue',
    'CARROCERIA': 'purple',
    'NEUMATICOS': 'brown',
    'OTRO': 'grey'
  };
  return colors[tipo] || 'grey';
};

const getSeveridadColor = (severidad) => {
  const colors = {
    'BAJA': 'success',
    'MEDIA': 'warning',
    'ALTA': 'error',
    'CRITICA': 'deep-orange'
  };
  return colors[severidad] || 'grey';
};

const getEstadoColor = (estado) => {
  const colors = {
    'PENDIENTE': 'warning',
    'EN_PROCESO': 'info',
    'RESUELTA': 'success',
    'CANCELADA': 'error'
  };
  return colors[estado] || 'grey';
};

const canResolve = (falla) => {
  return (authStore.isAdmin || authStore.isWorker) &&
    falla.estado === 'PENDIENTE';
};

const openDetailsDialog = (falla) => {
  selectedFalla.value = falla;
  dialogs.value.details = true;
};

const openResolveDialog = (falla) => {
  selectedFalla.value = falla;
  resolucionData.value = {
    solucion: ''
  };
  dialogs.value.resolve = true;
};

const closeResolveDialog = () => {
  dialogs.value.resolve = false;
  resolveForm.value?.reset();
  selectedFalla.value = null;
};

const resolverFalla = async () => {
  if (!formValid.value) return;

  saving.value = true;
  try {
    const mantenimientoService = useMantenimientoService();
    await mantenimientoService.resolverFalla(
      selectedFalla.value.id,
      resolucionData.value.solucion,
      authStore.user.id
    );

    snackbar.value = {
      show: true,
      color: 'success',
      text: 'Falla resuelta correctamente'
    };

    closeResolveDialog();
    await loadFallas();
    emit('falla-resuelta');
  } catch (error) {
    console.error('Error resolviendo falla:', error);
    snackbar.value = {
      show: true,
      color: 'error',
      text: error.message || 'Error al resolver la falla'
    };
  } finally {
    saving.value = false;
  }
};

// Lifecycle hooks
onMounted(() => {
  loadFallas();
});
</script>
