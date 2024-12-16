<template>
  <v-container fluid>
    <!-- Header con título y botón de agregar -->
    <v-row class="mb-4 header-row">
      <v-col
        cols="12"
        class="d-flex align-center"
      >
        <h2 class="text-h4">
          Gestión de Vehículos
        </h2>
        <v-spacer />
        <v-btn
          color="primary"
          prepend-icon="mdi-plus"
          @click="openVehicleDialog()"
        >
          Nuevo Vehículo
        </v-btn>
      </v-col>
    </v-row>

    <!-- Filtros con altura fija -->
    <v-card class="mb-4 filter-section">
      <v-card-text>
        <v-row>
          <v-col
            cols="12"
            md="3"
          >
            <v-select
              v-model="filters.sucursal"
              :items="sucursales"
              item-title="title"
              item-value="value"
              label="Filtrar por Sucursal"
              :loading="sucursalLoading"
              :error-messages="sucursalError"
              clearable
              class="fixed-height-input"
            />
          </v-col>
          <v-col
            cols="12"
            md="3"
          >
            <v-select
              v-model="filters.estado"
              :items="estadosVehiculo"
              label="Filtrar por Estado"
              clearable
              class="fixed-height-input"
            />
          </v-col>
          <v-col
            cols="12"
            md="3"
          >
            <v-text-field
              v-model="filters.search"
              label="Buscar"
              prepend-inner-icon="mdi-magnify"
              clearable
              class="fixed-height-input"
            />
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- Tabla con altura fija -->
    <v-card class="main-table-card">
      <v-data-table
        :headers="headers"
        :items="filteredVehicles"
        :loading="loading"
        :items-per-page="10"
        class="fixed-height-table"
        height="calc(100vh - 300px)"
        fixed-header
      >
        <!-- Estado del vehículo -->
        <template #[`item.estado`]="{ item }">
          <v-chip
            :color="getStatusColor(item.estado)"
            :text="getStatusText(item.estado)"
            size="small"
          />
        </template>

        <!-- Acciones -->
        <template #[`item.actions`]="{ item }">
          <v-btn
            v-if="canEdit(item)"
            icon="mdi-pencil"
            size="small"
            class="mr-2"
            @click="openVehicleDialog(item)"
          />
          <v-btn
            v-if="canReport(item)"
            icon="mdi-alert"
            size="small"
            color="warning"
            class="mr-2"
            @click="openReportDialog(item)"
          />
          <v-btn
            v-if="canDelete(item)"
            icon="mdi-delete"
            size="small"
            color="error"
            @click="confirmDelete(item)"
          />
        </template>
      </v-data-table>
    </v-card>

    <!-- Diálogo para crear/editar vehículo -->
    <v-dialog
      v-model="dialogs.vehicle"
      max-width="600"
      persistent
    >
      <v-card>
        <v-card-title>
          {{ isEditing ? 'Editar Vehículo' : 'Nuevo Vehículo' }}
        </v-card-title>
        <v-card-text>
          <v-form
            ref="vehicleForm"
            v-model="formValid"
          >
            <v-row>
              <v-col
                cols="12"
                md="6"
              >
                <v-text-field
                  v-model="vehicleData.marca"
                  label="Marca"
                  :rules="rules.required"
                />
              </v-col>
              <v-col
                cols="12"
                md="6"
              >
                <v-text-field
                  v-model="vehicleData.modelo"
                  label="Modelo"
                  :rules="rules.required"
                />
              </v-col>
              <v-col
                cols="12"
                md="6"
              >
                <v-text-field
                  v-model="vehicleData.patente"
                  label="Patente"
                  :rules="[...rules.required, rules.patente]"
                  :disabled="isEditing"
                />
              </v-col>
              <v-col
                cols="12"
                md="6"
              >
                <v-text-field
                  v-model="vehicleData.precioArriendo"
                  label="Precio Arriendo"
                  type="number"
                  prefix="$"
                  :rules="[...rules.required, rules.precio]"
                />
              </v-col>
              <v-col
                cols="12"
                md="6"
              >
                <v-select
                  v-model="vehicleData.sucursalId"
                  :items="sucursales"
                  item-title="title"
                  item-value="value"
                  label="Sucursal"
                  :rules="rules.required"
                  :loading="sucursalLoading"
                  :error-messages="sucursalError"
                  :disabled="sucursalLoading"
                />
              </v-col>
              <v-col
                cols="12"
                md="6"
              >
                <v-select
                  v-model="vehicleData.estado"
                  :items="estadosVehiculo"
                  label="Estado"
                  :rules="rules.required"
                />
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            color="grey"
            text
            @click="closeVehicleDialog"
          >
            Cancelar
          </v-btn>
          <v-btn
            color="primary"
            :disabled="!formValid"
            :loading="saving"
            @click="saveVehicle"
          >
            Guardar
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Diálogo para reportar fallas -->
    <v-dialog
      v-model="dialogs.report"
      max-width="600"
      persistent
    >
      <v-card>
        <v-card-title>Reportar Falla</v-card-title>
        <v-card-text>
          <v-form
            ref="reportForm"
            v-model="reportValid"
          >
            <v-textarea
              v-model="reportData.descripcion"
              label="Descripción de la falla"
              :rules="rules.required"
              rows="3"
            />
            <v-select
              v-model="reportData.tipoFalla"
              :items="tiposFalla"
              label="Tipo de falla"
              :rules="rules.required"
            />
            <v-select
              v-model="reportData.severidad"
              :items="nivelesServeridad"
              label="Severidad"
              :rules="rules.required"
            />
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            color="grey"
            text
            @click="closeReportDialog"
          >
            Cancelar
          </v-btn>
          <v-btn
            color="warning"
            :disabled="!reportValid"
            :loading="saving"
            @click="submitReport"
          >
            Reportar
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Diálogo de confirmación para eliminar -->
    <v-dialog
      v-model="dialogs.delete"
      max-width="400"
    >
      <v-card>
        <v-card-title>Confirmar Eliminación</v-card-title>
        <v-card-text>
          ¿Está seguro que desea eliminar este vehículo?
          Esta acción no se puede deshacer.
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            color="grey"
            text
            @click="dialogs.delete = false"
          >
            Cancelar
          </v-btn>
          <v-btn
            color="error"
            :loading="saving"
            @click="deleteVehicle"
          >
            Eliminar
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>
<script setup>
import {ref, computed, onMounted} from 'vue'
import {useVehicleStore} from '@/stores/vehicle'
import {useAuthStore} from '@/stores/auth'
import {useSucursalService} from '@/services/SucursalService'

// Stores
const vehicleStore = useVehicleStore()
const authStore = useAuthStore()

// Estado
const loading = ref(false)
const saving = ref(false)
const formValid = ref(false)
const reportValid = ref(false)
const vehicleForm = ref(null)
const reportForm = ref(null)

// Estado para sucursales
const sucursales = ref([])
const sucursalLoading = ref(false)
const sucursalError = ref(null)

// Datos de formularios
const vehicleData = ref({
  marca: '',
  modelo: '',
  patente: '',
  precioArriendo: '',
  sucursalId: null,
  estado: 'DISPONIBLE'
})

const reportData = ref({
  descripcion: '',
  tipoFalla: '',
  severidad: ''
})

// Control de diálogos
const dialogs = ref({
  vehicle: false,
  report: false,
  delete: false
})

// Estado de edición
const isEditing = ref(false)
const selectedVehicle = ref(null)

// Datos para selects
const estadosVehiculo = [
  'DISPONIBLE',
  'NO_DISPONIBLE',
  'EN_MANTENCION',
  'EN_REPARACION'
]

const tiposFalla = [
  'MECANICA',
  'ELECTRICA',
  'CARROCERIA',
  'NEUMATICOS',
  'OTRO'
]

const nivelesServeridad = [
  'BAJA',
  'MEDIA',
  'ALTA',
  'CRITICA'
]

// Reglas de validación
const rules = {
  required: [v => !!v || 'Este campo es requerido'],
  patente: v => /^[A-Z]{4}\d{2}$/.test(v) || 'Formato inválido (XXXX99)',
  precio: v => v > 0 || 'El precio debe ser mayor a 0'
}

// Filtros
const filters = ref({
  sucursal: null,
  estado: null,
  search: ''
})

// Headers para la tabla
const headers = [
  {title: 'Patente', key: 'patente'},
  {title: 'Marca', key: 'marca'},
  {title: 'Modelo', key: 'modelo'},
  {title: 'Precio', key: 'precioArriendo'},
  {title: 'Estado', key: 'estado'},
  {title: 'Acciones', key: 'actions', sortable: false}
]

// Método para cargar sucursales
const loadSucursales = async () => {
  sucursalLoading.value = true
  sucursalError.value = null
  try {
    const sucursalService = useSucursalService();
    const data = await sucursalService.listarSucursales();
    sucursales.value = data.map(s => ({
      title: s.nombre,
      value: s.id,
      direccion: s.direccion
    }));
  } catch (error) {
    console.error('Error cargando sucursales:', error);
    sucursalError.value = 'Error al cargar las sucursales';
  } finally {
    sucursalLoading.value = false;
  }
};

// Computed
const filteredVehicles = computed(() => {
  return vehicleStore.vehicles.filter(vehicle => {
    if (filters.value.sucursal && vehicle.sucursal?.id !== filters.value.sucursal) return false
    if (filters.value.estado && vehicle.estado !== filters.value.estado) return false
    if (filters.value.search) {
      const search = filters.value.search.toLowerCase()
      return vehicle.patente.toLowerCase().includes(search) ||
        vehicle.marca.toLowerCase().includes(search) ||
        vehicle.modelo.toLowerCase().includes(search)
    }
    return true
  })
})

// Métodos
onMounted(async () => {
  try {
    loading.value = true
    await Promise.all([
      loadData(),
      loadSucursales()
    ])
  } catch (error) {
    console.error('Error durante la inicialización:', error)
  } finally {
    loading.value = false
  }
})

const loadData = async () => {
  loading.value = true
  try {
    await vehicleStore.fetchVehicles()
  } finally {
    loading.value = false
  }
}

const openVehicleDialog = (vehicle = null) => {
  if (vehicle) {
    isEditing.value = true
    selectedVehicle.value = vehicle
    vehicleData.value = {
      ...vehicle,
      sucursalId: vehicle.sucursal?.id
    }
  } else {
    isEditing.value = false
    selectedVehicle.value = null
    vehicleData.value = {
      marca: '',
      modelo: '',
      patente: '',
      precioArriendo: '',
      sucursalId: null,
      estado: 'DISPONIBLE'
    }
  }
  dialogs.value.vehicle = true
}

const closeVehicleDialog = () => {
  dialogs.value.vehicle = false
  vehicleForm.value?.reset()
}

const openReportDialog = (vehicle) => {
  selectedVehicle.value = vehicle
  reportData.value = {
    descripcion: '',
    tipoFalla: '',
    severidad: ''
  }
  dialogs.value.report = true
}

const closeReportDialog = () => {
  dialogs.value.report = false
  reportForm.value?.reset()
  selectedVehicle.value = null
}

const submitReport = async () => {
  if (!reportForm.value?.validate()) return

  saving.value = true
  try {
    await vehicleStore.reportVehicleIssue({
      vehiculoId: selectedVehicle.value.id,
      ...reportData.value,
      estado: 'EN_MANTENCION'
    })
    closeReportDialog()
    await loadData()
  } finally {
    saving.value = false
  }
}
const confirmDelete = (vehicle) => {
  selectedVehicle.value = vehicle
  dialogs.value.delete = true
}

const deleteVehicle = async () => {
  saving.value = true
  try {
    await vehicleStore.deleteVehicle(selectedVehicle.value.id)
    dialogs.value.delete = false
    selectedVehicle.value = null
    await loadData()
  } finally {
    saving.value = false
  }
}

// Helpers
const getStatusColor = (status) => {
  const colors = {
    'DISPONIBLE': 'success',
    'NO_DISPONIBLE': 'error',
    'EN_MANTENCION': 'warning',
    'EN_REPARACION': 'orange'
  }
  return colors[status] || 'grey'
}

const getStatusText = (status) => {
  return status.replace('_', ' ')
}

const canEdit = (vehicle) => {
  return authStore.isAdmin
}

const canReport = (vehicle) => {
  return vehicle.estado === 'DISPONIBLE' &&
    (authStore.isAdmin || authStore.isWorker)
}

const canDelete = (vehicle) => {
  return authStore.isAdmin && vehicle.estado === 'DISPONIBLE'
}

const saveVehicle = async () => {
  if (!vehicleForm.value?.validate()) return;

  const vehiculoData = {
    ...vehicleData.value,
    sucursal: sucursales.value.find(s => s.value === vehicleData.value.sucursalId)
  };

  const {isValid, errors} = vehicleStore.validateVehicleData(vehiculoData);
  if (!isValid) {
    errors.forEach(error => {
      console.error(error);
    });
    return;
  }

  saving.value = true;
  try {
    if (isEditing.value) {
      await vehicleStore.updateVehicle(vehiculoData);
    } else {
      await vehicleStore.createVehicle(vehiculoData);
    }
    closeVehicleDialog();
    await loadData();
  } finally {
    saving.value = false;
  }
};
</script>
<style scoped>
.header-row {
  height: 64px;
}

.filter-section {
  height: 100px;
}

.main-table-card {
  height: calc(100vh - 300px);
  overflow: hidden;
}

.fixed-height-table {
  height: 100%;
}

/* Estilos de la tabla */
:deep(.v-data-table-header th) {
  height: 48px !important;
}

:deep(.v-data-table-row td) {
  height: 48px !important;
  padding: 0 16px !important;
}

/* Chip de estado */
:deep(.v-chip) {
  width: 120px;
  justify-content: center;
}

/* Botones de acción */
:deep(.v-btn--icon) {
  width: 36px !important;
  height: 36px !important;
}

/* Estilos del diálogo */
:deep(.v-dialog) {
  position: relative;
  z-index: 1000;
}

:deep(.v-card) {
  position: relative;
  z-index: 1001;
  background-color: white;
}

:deep(.v-text-field),
:deep(.v-select),
:deep(.v-textarea) {
  margin-bottom: 16px;
}

/* Responsive */
@media (max-width: 600px) {
  .header-row {
    height: 56px;
  }

  .filter-section {
    height: auto;
    min-height: 200px;
  }

  .main-table-card {
    height: calc(100vh - 400px);
  }
}
</style>
