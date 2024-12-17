<template>
  <v-card>
    <v-card-title class="d-flex justify-space-between align-center">
      <span>Calendario de Mantenimiento</span>
      <v-btn
        color="primary"
        prepend-icon="mdi-plus"
        @click="dialog.schedule = true"
      >
        Programar Mantenimiento
      </v-btn>
    </v-card-title>

    <v-data-table
      :headers="headers"
      :items="mantenimientos"
      :loading="loading"
    >
      <!-- Fecha Programada -->
      <template #item.fechaProgramada="{ item }">
        {{ formatDate(item.fechaProgramada) }}
      </template>

      <!-- Estado -->
      <template #item.estado="{ item }">
        <v-chip
          :color="getStateColor(item.estado)"
          size="small"
        >
          {{ item.estado }}
        </v-chip>
      </template>

      <!-- Costo -->
      <template #item.costo="{ item }">
        ${{ item.costo.toLocaleString() }}
      </template>

      <!-- Acciones -->
      <template #item.actions="{ item }">
        <v-btn
          v-if="canUpdateStatus(item)"
          icon="mdi-pencil"
          size="small"
          color="primary"
          @click="openUpdateStatus(item)"
        />
        <v-btn
          v-if="canViewDetails(item)"
          icon="mdi-eye"
          size="small"
          @click="openDetails(item)"
        />
      </template>
    </v-data-table>

    <!-- Diálogo para programar mantenimiento -->
    <v-dialog
      v-model="dialog.schedule"
      max-width="500px"
    >
      <v-card>
        <v-card-title>Programar Mantenimiento</v-card-title>
        <v-card-text>
          <v-form
            ref="scheduleForm"
            v-model="formValid"
          >
            <v-select
              v-model="formData.tipoMantenimiento"
              :items="tiposMantenimiento"
              label="Tipo de Mantenimiento"
              :rules="rules.required"
            />

            <v-textarea
              v-model="formData.descripcion"
              label="Descripción"
              :rules="rules.required"
            />

            <v-text-field
              v-model="formData.fechaProgramada"
              label="Fecha Programada"
              type="datetime-local"
              :rules="rules.required"
            />

            <v-text-field
              v-model="formData.costoEstimado"
              label="Costo Estimado"
              type="number"
              prefix="$"
              :rules="rules.costo"
            />

            <v-select
              v-model="formData.tecnicoId"
              :items="tecnicos"
              label="Técnico Asignado"
              item-title="nombre"
              item-value="id"
              :rules="rules.required"
            />
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            color="grey"
            text
            @click="dialog.schedule = false"
          >
            Cancelar
          </v-btn>
          <v-btn
            color="primary"
            :disabled="!formValid"
            :loading="saving"
            @click="scheduleMaintenence"
          >
            Programar
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Diálogo para actualizar estado -->
    <v-dialog
      v-model="dialog.status"
      max-width="400px"
    >
      <v-card>
        <v-card-title>Actualizar Estado</v-card-title>
        <v-card-text>
          <v-select
            v-model="selectedStatus"
            :items="estadosMantenimiento"
            label="Nuevo Estado"
            :rules="[v => !!v || 'El estado es requerido']"
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            color="grey"
            text
            @click="dialog.status = false"
          >
            Cancelar
          </v-btn>
          <v-btn
            color="primary"
            :loading="saving"
            @click="updateStatus"
          >
            Actualizar
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Diálogo de detalles -->
    <v-dialog
      v-model="dialog.details"
      max-width="600px"
    >
      <v-card v-if="selectedMaintenance">
        <v-card-title>Detalles del Mantenimiento</v-card-title>
        <v-card-text>
          <v-list>
            <v-list-item>
              <v-list-item-title>Tipo</v-list-item-title>
              <v-list-item-subtitle>{{ selectedMaintenance.tipoMantenimiento }}</v-list-item-subtitle>
            </v-list-item>

            <v-list-item>
              <v-list-item-title>Descripción</v-list-item-title>
              <v-list-item-subtitle>{{ selectedMaintenance.descripcion }}</v-list-item-subtitle>
            </v-list-item>

            <v-list-item>
              <v-list-item-title>Fecha Programada</v-list-item-title>
              <v-list-item-subtitle>{{ formatDate(selectedMaintenance.fechaProgramada) }}</v-list-item-subtitle>
            </v-list-item>

            <v-list-item v-if="selectedMaintenance.fechaRealizada">
              <v-list-item-title>Fecha Realizada</v-list-item-title>
              <v-list-item-subtitle>{{ formatDate(selectedMaintenance.fechaRealizada) }}</v-list-item-subtitle>
            </v-list-item>

            <v-list-item>
              <v-list-item-title>Costo</v-list-item-title>
              <v-list-item-subtitle>${{ selectedMaintenance.costo.toLocaleString() }}</v-list-item-subtitle>
            </v-list-item>

            <v-list-item>
              <v-list-item-title>Estado</v-list-item-title>
              <v-list-item-subtitle>
                <v-chip
                  :color="getStateColor(selectedMaintenance.estado)"
                  size="small"
                >
                  {{ selectedMaintenance.estado }}
                </v-chip>
              </v-list-item-subtitle>
            </v-list-item>
          </v-list>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            color="primary"
            text
            @click="dialog.details = false"
          >
            Cerrar
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-card>
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
</template>
<script setup>
import {ref, onMounted, watch} from 'vue'
import {useMantenimientoService} from '@/services/MantenimientoService';
import {useUsuarioService} from '@/services/UsuarioService';

const props = defineProps({
  vehiculoId: {
    type: Number,
    required: true
  },
  activeTab: {
    type: String,
    default: 'maintenance'
  }
});

// Emitir eventos al padre
const emit = defineEmits(['update:activeTab', 'refresh-history']);

// Estado
const loading = ref(false)
const saving = ref(false)
const formValid = ref(false)
const mantenimientos = ref([])
const tecnicos = ref([])
const selectedMaintenance = ref(null)
const selectedStatus = ref(null)
const historyRefreshTrigger = ref(false)

// Referencias
const scheduleForm = ref(null)

// Diálogos
const dialog = ref({
  schedule: false,
  status: false,
  details: false
})

// Formulario
const formData = ref({
  tipoMantenimiento: '',
  descripcion: '',
  fechaProgramada: '',
  costoEstimado: 0,
  tecnicoId: null
});

// Reglas de validación
const rules = {
  required: [v => !!v || 'Este campo es requerido'],
  fecha: [
    v => !!v || 'La fecha es requerida',
    v => new Date(v) > new Date() || 'La fecha debe ser futura'
  ],
  costo: [
    v => v >= 0 || 'El costo debe ser mayor o igual a 0'
  ]
};

// Datos estáticos
const tiposMantenimiento = [
  'PREVENTIVO',
  'CORRECTIVO'
]

const estadosMantenimiento = [
  'PENDIENTE',
  'EN_PROCESO',
  'COMPLETADO',
  'CANCELADO'
]

const headers = [
  {title: 'Tipo', key: 'tipoMantenimiento'},
  {title: 'Fecha Programada', key: 'fechaProgramada'},
  {title: 'Estado', key: 'estado'},
  {title: 'Costo', key: 'costo'},
  {title: 'Acciones', key: 'actions', sortable: false}
]

// Snackbar
const snackbar = ref({
  show: false,
  text: '',
  color: 'success'
});

// Métodos
const loadMaintenence = async () => {
  loading.value = true;
  try {
    const mantenimientoService = useMantenimientoService();
    mantenimientos.value = await mantenimientoService.obtenerMantenimientos(props.vehiculoId);
  } catch (error) {
    console.error('Error cargando mantenimientos:', error);
    snackbar.value = {
      show: true,
      color: 'error',
      text: 'Error al cargar mantenimientos'
    };
  } finally {
    loading.value = false;
  }
};

const loadTecnicos = async () => {
  try {
    const usuarioService = useUsuarioService();
    tecnicos.value = await usuarioService.obtenerTrabajadores();
  } catch (error) {
    console.error('Error cargando técnicos:', error);
    snackbar.value = {
      show: true,
      color: 'error',
      text: 'Error al cargar técnicos'
    };
  }
};

const formatDate = (date) => {
  return new Date(date).toLocaleString()
}

const getStateColor = (estado) => {
  const colors = {
    'PENDIENTE': 'warning',
    'EN_PROCESO': 'info',
    'COMPLETADO': 'success',
    'CANCELADO': 'error'
  }
  return colors[estado] || 'grey'
}

const canUpdateStatus = (item) => {
  return ['PENDIENTE', 'EN_PROCESO'].includes(item.estado)
}

const canViewDetails = () => true

const openUpdateStatus = (item) => {
  selectedMaintenance.value = item
  selectedStatus.value = item.estado
  dialog.value.status = true
}

const openDetails = (item) => {
  selectedMaintenance.value = item
  dialog.value.details = true
}

const scheduleMaintenence = async () => {
  if (!formValid.value) return;

  saving.value = true;
  try {
    const mantenimientoData = {
      vehiculoId: props.vehiculoId,
      fechaProgramada: formData.value.fechaProgramada,
      tipoMantenimiento: formData.value.tipoMantenimiento,
      descripcion: formData.value.descripcion,
      costoEstimado: Number(formData.value.costoEstimado),
      tecnicoId: formData.value.tecnicoId
    };

    const mantenimientoService = useMantenimientoService();
    await mantenimientoService.programarMantenimiento(mantenimientoData);

    dialog.value.schedule = false;
    await loadMaintenence();
    emit('refresh-history');

    snackbar.value = {
      show: true,
      color: 'success',
      text: 'Mantenimiento programado correctamente'
    };
  } catch (error) {
    console.error('Error:', error);
    snackbar.value = {
      show: true,
      color: 'error',
      text: error.response?.data || 'Error al programar mantenimiento'
    };
  } finally {
    saving.value = false;
  }
};

const updateStatus = async () => {
  saving.value = true;
  try {
    const mantenimientoService = useMantenimientoService();
    await mantenimientoService.actualizarEstado(
      selectedMaintenance.value.id,
      selectedStatus.value
    );

    snackbar.value = {
      show: true,
      color: 'success',
      text: 'Estado actualizado correctamente'
    };

    await loadMaintenence();
    emit('refresh-history');
    dialog.value.status = false;
  } catch (error) {
    console.error('Error:', error);
    snackbar.value = {
      show: true,
      color: 'error',
      text: error.response?.data || 'Error al actualizar el estado'
    };
  } finally {
    saving.value = false;
  }
};

// Watchers
watch(() => props.activeTab, (newTab) => {
  if (newTab === 'maintenance') {
    loadMaintenence();
  }
});

// Lifecycle hooks
onMounted(async () => {
  await Promise.all([
    loadMaintenence(),
    loadTecnicos()
  ])
});
</script>
<style scoped>
.v-data-table {
  width: 100%;
}

.v-list-item {
  padding: 8px 0;
}

.v-list-item-title {
  font-weight: bold;
  margin-bottom: 4px;
}
</style>
