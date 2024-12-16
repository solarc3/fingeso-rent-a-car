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
              <!-- Campos existentes -->
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

              <!-- Nuevos campos -->
              <v-col
                cols="12"
                md="6"
              >
                <v-text-field
                  v-model="vehicleData.anio"
                  label="Año"
                  type="number"
                  :rules="[...rules.required, rules.anio]"
                />
              </v-col>

              <v-col
                cols="12"
                md="6"
              >
                <v-select
                  v-model="vehicleData.tipoVehiculo"
                  :items="metadataStore.tiposVehiculo"
                  label="Tipo de Vehículo"
                  item-title="texto"
                  item-value="valor"
                  :rules="rules.required"
                />
              </v-col>

              <v-col
                cols="12"
                md="6"
              >
                <v-select
                  v-model="vehicleData.transmision"
                  :items="metadataStore.tiposTransmision"
                  label="Transmisión"
                  item-title="texto"
                  item-value="valor"
                  :rules="rules.required"
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

              <!-- Sección de fallas (aparece si el estado es EN_MANTENCION o EN_REPARACION) -->
              <v-col
                v-if="tieneFallas"
                cols="12"
              >
                <v-card outlined>
                  <v-card-title class="text-subtitle-1">
                    Detalles de la Falla
                  </v-card-title>
                  <v-card-text>
                    <v-row>
                      <v-col cols="12">
                        <v-select
                          v-model="vehicleData.falla.tipo"
                          :items="tiposFalla"
                          label="Tipo de Falla"
                          :rules="rules.required"
                        />
                      </v-col>
                      <v-col cols="12">
                        <v-select
                          v-model="vehicleData.falla.severidad"
                          :items="nivelesSeveridad"
                          label="Severidad"
                          :rules="rules.required"
                        />
                      </v-col>
                      <v-col cols="12">
                        <v-textarea
                          v-model="vehicleData.falla.descripcion"
                          label="Descripción de la Falla"
                          :rules="rules.required"
                          rows="3"
                        />
                      </v-col>
                    </v-row>
                  </v-card-text>
                </v-card>
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>
        <!-- Botones -->
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
            {{ isEditing ? 'Actualizar' : 'Guardar' }}
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
            <v-select
              v-model="reportData.tipoFalla"
              :items="tiposFalla"
              label="Tipo de Falla"
              :rules="rules.required"
            />

            <v-select
              v-model="reportData.severidad"
              :items="nivelesSeveridad"
              label="Severidad"
              :rules="rules.required"
            />

            <v-textarea
              v-model="reportData.descripcion"
              label="Descripción"
              :rules="rules.required"
              rows="3"
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
            @click="handleReportarFalla"
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
            <v-tab value="fallas">
              Fallas
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
            <v-window-item value="fallas">
              <FallasListas
                :vehiculo-id="selectedVehicle.id"
                @falla-resuelta="refreshHistory"
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
import {ref, computed, onMounted} from 'vue';
import {useVehicleStore} from '@/stores/vehicle';
import {useAuthStore} from '@/stores/auth';
import {useMetadataStore} from '@/stores/metadata';
import {useSucursalService} from '@/services/SucursalService';
import VehicleHistory from '@/components/vehicles/VehicleHistory.vue';
import MaintenanceSchedule from '@/components/vehicles/MaintenceSchedule.vue';
import FallasListas from "@/components/vehicles/FallasListas.vue";

// Stores
const vehicleStore = useVehicleStore();
const authStore = useAuthStore();
const metadataStore = useMetadataStore();

// Estado
const loading = ref(false);
const saving = ref(false);
const formValid = ref(false);
const reportValid = ref(false);
const vehicleForm = ref(null);
const reportForm = ref(null);
const historyRefreshTrigger = ref(false);
const activeTab = ref('info');
const isEditing = ref(false);
const selectedVehicle = ref(null);

// Estado para sucursales
const sucursales = ref([]);
const sucursalLoading = ref(false);
const sucursalError = ref(null);

// Dialogs
const dialogs = ref({
  vehicle: false,
  report: false,
  delete: false,
  details: false
});

// Datos de formularios
const reportData = ref({
  descripcion: '',
  tipoFalla: '',
  severidad: ''
});

const tiposFalla = [
  'MECANICA',
  'ELECTRICA',
  'CARROCERIA',
  'NEUMATICOS',
  'OTRO'
];

const nivelesSeveridad = [
  'BAJA',
  'MEDIA',
  'ALTA',
  'CRITICA'
];

const estadosVehiculo = [
  'DISPONIBLE',
  'NO_DISPONIBLE',
  'EN_MANTENCION',
  'EN_REPARACION'
];

// Vehicle Data
const vehicleData = ref({
  marca: '',
  modelo: '',
  patente: '',
  anio: new Date().getFullYear(),
  tipoVehiculo: '',
  transmision: '',
  precioArriendo: '',
  sucursalId: null,
  estado: 'DISPONIBLE',
  falla: {
    tipo: '',
    severidad: '',
    descripcion: ''
  }
});

// Reglas de validación
const rules = {
  required: [v => !!v || 'Este campo es requerido'],
  patente: v => /^[A-Z]{4}\d{2}$/.test(v) || 'Formato inválido (XXXX99)',
  precio: v => v > 0 || 'El precio debe ser mayor a 0',
  anio: v => {
    const currentYear = new Date().getFullYear();
    return v >= 2000 && v <= currentYear + 1 || 'Año inválido';
  }
};

// Filtros
const filters = ref({
  sucursal: null,
  estado: null,
  search: ''
});

// Headers para la tabla
const headers = [
  {title: 'Patente', key: 'patente'},
  {title: 'Marca', key: 'marca'},
  {title: 'Modelo', key: 'modelo'},
  {title: 'Precio', key: 'precioArriendo'},
  {title: 'Estado', key: 'estado'},
  {title: 'Acciones', key: 'actions', sortable: false}
];

// Snackbar
const snackbar = ref({
  show: false,
  text: '',
  color: 'success'
});

// Computed Properties
const tieneFallas = computed(() => {
  return ['EN_MANTENCION', 'EN_REPARACION'].includes(vehicleData.value.estado);
});

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

// Methods
const refreshHistory = () => {
  historyRefreshTrigger.value = !historyRefreshTrigger.value;
};

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
  } catch (error) {
    console.error('Error cargando sucursales:', error);
    sucursalError.value = 'Error al cargar las sucursales';
  } finally {
    sucursalLoading.value = false;
  }
};

const loadData = async () => {
  loading.value = true;
  try {
    await vehicleStore.fetchVehicles();
  } finally {
    loading.value = false;
  }
};

const openVehicleDialog = (vehicle = null) => {
  if (vehicle) {
    isEditing.value = true;
    selectedVehicle.value = vehicle;

    const sucursalId = typeof vehicle.sucursal === 'number'
        ? vehicle.sucursal
        : vehicle.sucursal?.id || null;

    // Corregir el mapeo de los datos
    vehicleData.value = {
      id: vehicle.id,
      marca: vehicle.marca,
      modelo: vehicle.modelo,
      patente: vehicle.patente,
      anio: vehicle.anio,
      tipoVehiculo: typeof vehicle.tipoVehiculo === 'object'
          ? vehicle.tipoVehiculo.valor
          : vehicle.tipoVehiculo,
      transmision: typeof vehicle.transmision === 'object'
          ? vehicle.transmision.valor
          : vehicle.transmision,
      precioArriendo: vehicle.precioArriendo,
      estado: vehicle.estado,
      sucursalId: sucursalId,
      falla: {
        tipo: '',
        severidad: '',
        descripcion: ''
      }
    };
  } else {
    // Crear nuevo vehículo
    isEditing.value = false;
    selectedVehicle.value = null;
    vehicleData.value = {
      marca: '',
      modelo: '',
      patente: '',
      anio: new Date().getFullYear(),
      tipoVehiculo: '',
      transmision: '',
      precioArriendo: '',
      sucursalId: null,
      estado: 'DISPONIBLE',
      falla: {
        tipo: '',
        severidad: '',
        descripcion: ''
      }
    };
  }
  dialogs.value.vehicle = true;
};

const closeVehicleDialog = () => {
  dialogs.value.vehicle = false;
  vehicleForm.value?.reset();
};

const openDetailsDialog = (vehicle) => {
  selectedVehicle.value = vehicle;
  activeTab.value = 'info';
  dialogs.value.details = true;
};

const closeDetailsDialog = () => {
  dialogs.value.details = false;
  selectedVehicle.value = null;
  activeTab.value = 'info';
};

const openReportDialog = (vehicle) => {
  selectedVehicle.value = vehicle;
  reportData.value = {
    descripcion: '',
    tipoFalla: '',
    severidad: ''
  };
  dialogs.value.report = true;
};

const closeReportDialog = () => {
  dialogs.value.report = false;
  reportForm.value?.reset();
  selectedVehicle.value = null;
};


const confirmDelete = (vehicle) => {
  selectedVehicle.value = vehicle;
  dialogs.value.delete = true;
};

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
    dialogs.value.delete = false;
  } finally {
    saving.value = false;
  }
};

const saveVehicle = async () => {
  if (!vehicleForm.value?.validate()) return;

  const vehiculoData = {
    marca: vehicleData.value.marca,
    modelo: vehicleData.value.modelo,
    patente: vehicleData.value.patente,
    anio: Number(vehicleData.value.anio),
    tipoVehiculo: vehicleData.value.tipoVehiculo,
    transmision: vehicleData.value.transmision,
    precioArriendo: Number(vehicleData.value.precioArriendo),
    sucursal: Number(vehicleData.value.sucursalId),
    estado: vehicleData.value.estado || 'DISPONIBLE'
  };

  saving.value = true;
  try {
    if (isEditing.value) {
      vehiculoData.id = selectedVehicle.value.id;
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

const handleReportarFalla = async () => {
  if (!reportForm.value?.validate()) return;

  saving.value = true;
  try {
    await vehicleStore.reportarFalla({
      vehiculoId: selectedVehicle.value.id,
      tipo: reportData.value.tipoFalla,
      severidad: reportData.value.severidad,
      descripcion: reportData.value.descripcion,
      reportadoPorId: authStore.user.id
    });

    snackbar.value = {
      show: true,
      color: 'success',
      text: 'Falla reportada correctamente'
    };

    closeReportDialog();
    await loadData();
    refreshHistory();
  } catch (error) {
    console.error('Error al reportar falla:', error);
    snackbar.value = {
      show: true,
      color: 'error',
      text: error.message || 'Error al reportar la falla'
    };
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
  };
  return colors[status] || 'grey';
};

const getStatusText = (status) => {
  return status.replace('_', ' ');
};

const canEdit = () => {
  return authStore.isAdmin;
};

const canReport = (vehicle) => {
  return vehicle.estado === 'DISPONIBLE' &&
      (authStore.isAdmin || authStore.isWorker);
};

const canDelete = (vehicle) => {
  return authStore.isAdmin && vehicle.estado === 'DISPONIBLE';
};


// Watchers
watch(() => vehicleData.value.sucursalId, (newVal, oldVal) => {
  console.log('Sucursal ID changed:', {
    from: oldVal,
    to: newVal,
    type: typeof newVal
  });
});

// Lifecycle Hooks
onMounted(async () => {
  try {
    loading.value = true;
    await Promise.all([
      loadData(),
      loadSucursales(),
      metadataStore.loadMetadata() // Cargar metadata al montar el componente
    ]);
  } catch (error) {
    console.error('Error durante la inicialización:', error);
  } finally {
    loading.value = false;
  }
});
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
