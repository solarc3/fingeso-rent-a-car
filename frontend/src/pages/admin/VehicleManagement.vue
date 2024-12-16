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
            icon="mdi-information"
            size="small"
            class="mr-2"
            color="info"
            @click="openDetailsDialog(item)"
          />
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
                  persistent-hint
                  :hint="vehicleData.sucursalId ? `Sucursal actual: ${getSucursalName(vehicleData.sucursalId)}` : 'Seleccione una sucursal'"
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
    <v-dialog
      v-model="dialogs.details"
      max-width="900"
      persistent
    >
      <v-card v-if="selectedVehicle">
        <v-card-title class="d-flex justify-space-between align-center">
          <span>{{ selectedVehicle.marca }} {{ selectedVehicle.modelo }} - {{ selectedVehicle.patente }}</span>
          <v-btn
            icon="mdi-close"
            size="small"
            @click="closeDetailsDialog"
          />
        </v-card-title>

        <v-card-text>
          <v-tabs v-model="activeTab">
            <v-tab value="info">
              Información
            </v-tab>
            <v-tab value="history">
              Historial
            </v-tab>
            <v-tab value="maintenance">
              Mantenimiento
            </v-tab>
          </v-tabs>

          <v-window v-model="activeTab">
            <!-- Tab de Información -->
            <v-window-item value="info">
              <v-card flat>
                <v-card-text>
                  <v-list>
                    <v-list-item>
                      <v-list-item-title class="font-weight-bold">
                        Marca
                      </v-list-item-title>
                      <v-list-item-subtitle>{{ selectedVehicle.marca }}</v-list-item-subtitle>
                    </v-list-item>

                    <v-list-item>
                      <v-list-item-title class="font-weight-bold">
                        Modelo
                      </v-list-item-title>
                      <v-list-item-subtitle>{{ selectedVehicle.modelo }}</v-list-item-subtitle>
                    </v-list-item>

                    <v-list-item>
                      <v-list-item-title class="font-weight-bold">
                        Patente
                      </v-list-item-title>
                      <v-list-item-subtitle>{{ selectedVehicle.patente }}</v-list-item-subtitle>
                    </v-list-item>

                    <v-list-item>
                      <v-list-item-title class="font-weight-bold">
                        Año
                      </v-list-item-title>
                      <v-list-item-subtitle>{{ selectedVehicle.anio }}</v-list-item-subtitle>
                    </v-list-item>

                    <v-list-item>
                      <v-list-item-title class="font-weight-bold">
                        Precio de Arriendo
                      </v-list-item-title>
                      <v-list-item-subtitle>
                        ${{
                          selectedVehicle.precioArriendo.toLocaleString()
                        }}
                      </v-list-item-subtitle>
                    </v-list-item>

                    <v-list-item>
                      <v-list-item-title class="font-weight-bold">
                        Sucursal
                      </v-list-item-title>
                      <v-list-item-subtitle>{{ vehicleStore.getSucursalName(selectedVehicle) }}</v-list-item-subtitle>
                    </v-list-item>

                    <v-list-item>
                      <v-list-item-title class="font-weight-bold">
                        Estado
                      </v-list-item-title>
                      <v-list-item-subtitle>
                        <v-chip
                          :color="getStatusColor(selectedVehicle.estado)"
                          size="small"
                        >
                          {{ getStatusText(selectedVehicle.estado) }}
                        </v-chip>
                      </v-list-item-subtitle>
                    </v-list-item>
                  </v-list>
                </v-card-text>
              </v-card>
            </v-window-item>

            <!-- Tab de Historial -->
            <v-window-item value="history">
              <VehicleHistory
                :vehiculo-id="selectedVehicle.id"
                :should-refresh="historyRefreshTrigger"
              />
            </v-window-item>

            <!-- Tab de Mantenimiento -->
            <v-window-item value="maintenance">
              <MaintenanceSchedule
                :vehiculo-id="selectedVehicle.id"
                :active-tab="activeTab"
                @refresh-history="refreshHistory"
              />
            </v-window-item>
          </v-window>
        </v-card-text>
      </v-card>
    </v-dialog>
  </v-container>

  
  <v-snackbar
    v-model="snackbar.show"
    :color="snackbar.color"
    :timeout="6000"
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
</template>
<script setup>
import {watch} from 'vue';
import {ref, computed, onMounted} from 'vue'
import {useVehicleStore} from '@/stores/vehicle'
import {useAuthStore} from '@/stores/auth'
import {useSucursalService} from '@/services/SucursalService'
import VehicleHistory from '@/components/vehicles/VehicleHistory.vue'
import MaintenanceSchedule from '@/components/vehicles/MaintenceSchedule.vue'
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
const historyRefreshTrigger = ref(false);

const refreshHistory = () => {
  historyRefreshTrigger.value = !historyRefreshTrigger.value;
};
// Estado para sucursales
const sucursales = ref([])
const sucursalLoading = ref(false)
const sucursalError = ref(null)

// Datos de formularios
const activeTab = ref('info')
const dialogs = ref({
  vehicle: false,
  report: false,
  delete: false,
  details: false
})

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
const openDetailsDialog = (vehicle) => {
  selectedVehicle.value = vehicle
  activeTab.value = 'info'
  dialogs.value.details = true
}
const closeDetailsDialog = () => {
  dialogs.value.details = false
  selectedVehicle.value = null
  activeTab.value = 'info'
}
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
const getSucursalName = (sucursalId) => {
  // Asegurarse de que sucursalId sea un número
  const id = typeof sucursalId === 'object' ? sucursalId.id : Number(sucursalId);
  const sucursal = sucursales.value.find(s => s.value === id);
  return sucursal ? sucursal.title : 'No asignada';
};
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
watch(() => vehicleData.value.sucursalId, (newVal, oldVal) => {
  console.log('Sucursal ID changed:', {
    from: oldVal,
    to: newVal,
    type: typeof newVal
  });
});
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
  sucursalLoading.value = true;
  sucursalError.value = null;
  try {
    const sucursalService = useSucursalService();
    const data = await sucursalService.listarSucursales();

    sucursales.value = data.map(s => ({
      title: s.nombre,
      value: s.id,
      direccion: s.direccion
    }));

    console.log('Sucursales disponibles:', sucursales.value);
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
    const vehicleSucursalId = typeof vehicle.sucursal === 'number'
        ? vehicle.sucursal
        : vehicle.sucursal?.id;

    if (filters.value.sucursal && vehicleSucursalId !== filters.value.sucursal) return false;
    if (filters.value.estado && vehicle.estado !== filters.value.estado) return false;
    if (filters.value.search) {
      const search = filters.value.search.toLowerCase();
      return vehicle.patente.toLowerCase().includes(search) ||
          vehicle.marca.toLowerCase().includes(search) ||
          vehicle.modelo.toLowerCase().includes(search);
    }
    return true;
  });
});

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
  saving.value = true;
  try {
    await vehicleStore.deleteVehicle(selectedVehicle.value.id);
    dialogs.value.delete = false;
    selectedVehicle.value = null;
    snackbar.value = {
      show: true,
      color: 'success',
      text: 'Vehículo eliminado correctamente'
    };
    await loadData();
  } catch (error) {
    snackbar.value = {
      show: true,
      color: 'error',
      text: error.message || 'Error al eliminar el vehículo'
    };
    // Cerrar el diálogo de confirmación
    dialogs.value.delete = false;
  } finally {
    saving.value = false;
  }
};

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

const canEdit = () => {
  return authStore.isAdmin
}

const canReport = (vehicle) => {
  return vehicle.estado === 'DISPONIBLE' &&
      (authStore.isAdmin || authStore.isWorker)
}

const canDelete = (vehicle) => {
  return authStore.isAdmin && vehicle.estado === 'DISPONIBLE'
}

const openVehicleDialog = (vehicle = null) => {
  if (vehicle) {
    isEditing.value = true;
    selectedVehicle.value = vehicle;

    // Obtener el ID de la sucursal de manera segura
    const sucursalId = typeof vehicle.sucursal === 'number'
        ? vehicle.sucursal
        : vehicle.sucursal?.id || null;

    // Asignar todos los datos incluyendo la sucursal
    vehicleData.value = {
      id: vehicle.id,
      marca: vehicle.marca,
      modelo: vehicle.modelo,
      patente: vehicle.patente,
      precioArriendo: vehicle.precioArriendo,
      estado: vehicle.estado,
      sucursalId: sucursalId
    };

  } else {
    isEditing.value = false;
    selectedVehicle.value = null;
    vehicleData.value = {
      marca: '',
      modelo: '',
      patente: '',
      precioArriendo: '',
      sucursalId: null,
      estado: 'DISPONIBLE'
    };
  }
  dialogs.value.vehicle = true;
};
const snackbar = ref({
  show: false,
  text: '',
  color: 'success'
});

const saveVehicle = async () => {
  if (!vehicleForm.value?.validate()) return;

  const vehiculoData = {
    marca: vehicleData.value.marca,
    modelo: vehicleData.value.modelo,
    patente: vehicleData.value.patente,
    precioArriendo: Number(vehicleData.value.precioArriendo),
    sucursal: Number(vehicleData.value.sucursalId),
    estado: vehicleData.value.estado || 'DISPONIBLE'
  };

  if (isEditing.value) {
    vehiculoData.id = selectedVehicle.value.id;
  }

  saving.value = true;
  try {
    if (isEditing.value) {
      await vehicleStore.updateVehicle(vehiculoData);
      snackbar.value = {
        show: true,
        color: 'success',
        text: 'Vehículo actualizado correctamente'
      };
    } else {
      await vehicleStore.createVehicle(vehiculoData);
      snackbar.value = {
        show: true,
        color: 'success',
        text: 'Vehículo creado correctamente'
      };
    }
    closeVehicleDialog();
    await loadData();
  } catch (error) {
    snackbar.value = {
      show: true,
      color: 'error',
      text: `Error: ${error.message}`
    };
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

:deep(.v-snackbar__content) {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.v-window {
  margin-top: 20px;
}

.v-list-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 16px;
}

.v-list-item-title {
  font-weight: bold;
  color: rgba(0, 0, 0, 0.6);
}

.v-window-item {
  padding: 20px;
}
</style>
