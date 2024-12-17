<template>
  <v-container fluid>
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

    <v-card class="mb-4 filter-section">
      <v-card-text>
        <v-row>
          <v-col
            cols="12"
            md="3"
          >
            <v-text-field
              v-model="filters.search"
              label="Buscar por patente/marca/modelo"
              prepend-inner-icon="mdi-magnify"
              clearable
              hide-details
              class="fixed-height-input"
            />
          </v-col>

          <v-col
            cols="12"
            md="3"
          >
            <v-select
              v-model="filters.sucursal"
              :items="sucursales"
              item-title="title"
              item-value="value"
              label="Sucursal"
              :loading="sucursalLoading"
              :error-messages="sucursalError"
              clearable
              hide-details
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
              label="Estado"
              clearable
              hide-details
              class="fixed-height-input"
            >
              <template #selection="{ item }">
                <v-chip
                  :color="getStatusColor(item.raw)"
                  size="small"
                  class="mr-2"
                >
                  {{ getStatusText(item.raw) }}
                </v-chip>
              </template>
              <template #item="{ item, props }">
                <v-list-item v-bind="props">
                  <template #prepend>
                    <v-chip
                      :color="getStatusColor(item.raw)"
                      size="small"
                      class="mr-2"
                    >
                      {{ getStatusText(item.raw) }}
                    </v-chip>
                  </template>
                </v-list-item>
              </template>
            </v-select>
          </v-col>

          <v-col
            cols="12"
            md="3"
          >
            <v-select
              v-model="filters.ordenarPor"
              :items="opcionesOrdenamiento"
              item-title="title"
              item-value="value"
              label="Ordenar por"
              clearable
              hide-details
              class="fixed-height-input"
            />
          </v-col>
        </v-row>

        <v-row class="mt-4">
          <v-col
            cols="12"
            md="6"
          >
            <v-range-slider
              v-model="filters.rangoPrecio"
              :min="precioMinimo"
              :max="precioMaximo"
              :step="5000"
              label="Rango de Precio"
              thumb-label="always"
              hide-details
            >
              <template #prepend>
                <v-text-field
                  v-model="filters.rangoPrecio[0]"
                  type="number"
                  prefix="$"
                  hide-details
                  density="compact"
                  style="width: 100px"
                />
              </template>
              <template #append>
                <v-text-field
                  v-model="filters.rangoPrecio[1]"
                  type="number"
                  prefix="$"
                  hide-details
                  density="compact"
                  style="width: 100px"
                />
              </template>
            </v-range-slider>
          </v-col>

          <v-col
            cols="12"
            md="6"
            class="d-flex align-center justify-end"
          >
            <v-btn
              v-if="hasActiveFilters"
              color="error"
              variant="text"
              size="small"
              prepend-icon="mdi-filter-off"
              @click="clearFilters"
            >
              Limpiar filtros
            </v-btn>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

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
        <template #item.precioArriendo="{ item }">
          ${{ formatPrice(item.precioArriendo) }}
        </template>

        <template #item.estado="{ item }">
          <v-chip
            :color="getStatusColor(item.estado)"
            size="small"
          >
            {{ getStatusText(item.estado) }}
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
            v-if="canEdit(item)"
            text="Editar"
          >
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                icon="mdi-pencil"
                size="small"
                class="mr-2"
                @click="openVehicleDialog(item)"
              />
            </template>
          </v-tooltip>

          <v-tooltip
            v-if="canReport(item)"
            text="Reportar falla"
          >
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                icon="mdi-alert"
                size="small"
                color="warning"
                class="mr-2"
                @click="openReportDialog(item)"
              />
            </template>
          </v-tooltip>

          <v-tooltip
            v-if="canDelete(item)"
            text="Eliminar"
          >
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                icon="mdi-delete"
                size="small"
                color="error"
                @click="confirmDelete(item)"
              />
            </template>
          </v-tooltip>
        </template>
      </v-data-table>
    </v-card>

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
                  hide-details="auto"
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
                  hide-details="auto"
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
                  hide-details="auto"
                  maxlength="6"
                  @input="e => vehicleData.patente = e.target.value.toUpperCase()"
                />
              </v-col>

              <v-col
                cols="12"
                md="6"
              >
                <v-text-field
                  v-model="vehicleData.anio"
                  label="Año"
                  type="number"
                  :rules="[...rules.required, rules.anio]"
                  hide-details="auto"
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
                  hide-details="auto"
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
                  hide-details="auto"
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
                  hide-details="auto"
                >
                  <template #selection="{ item }">
                    <v-chip
                      :color="getStatusColor(item.raw)"
                      size="small"
                      class="mr-2"
                    >
                      {{ getStatusText(item.raw) }}
                    </v-chip>
                  </template>
                  <template #item="{ item, props }">
                    <v-list-item v-bind="props">
                      <template #prepend>
                        <v-chip
                          :color="getStatusColor(item.raw)"
                          size="small"
                          class="mr-2"
                        >
                          {{ getStatusText(item.raw) }}
                        </v-chip>
                      </template>
                    </v-list-item>
                  </template>
                </v-select>
              </v-col>

              <!-- fallas (solo si el estado es EN_MANTENCION o EN_REPARACION) -->
              <v-col
                v-if="tieneFallas"
                cols="12"
              >
                <v-card
                  variant="outlined"
                  class="pa-4"
                >
                  <v-card-title class="text-subtitle-1 px-0">
                    Detalles de la Falla
                  </v-card-title>
                  <v-card-text class="px-0">
                    <v-row>
                      <v-col
                        cols="12"
                        md="6"
                      >
                        <v-select
                          v-model="vehicleData.falla.tipo"
                          :items="tiposFalla"
                          label="Tipo de Falla"
                          :rules="rules.required"
                          hide-details="auto"
                        />
                      </v-col>

                      <v-col
                        cols="12"
                        md="6"
                      >
                        <v-select
                          v-model="vehicleData.falla.severidad"
                          :items="nivelesSeveridad"
                          label="Severidad"
                          :rules="rules.required"
                          hide-details="auto"
                        />
                      </v-col>

                      <v-col cols="12">
                        <v-textarea
                          v-model="vehicleData.falla.descripcion"
                          label="Descripción de la Falla"
                          :rules="rules.required"
                          rows="3"
                          hide-details="auto"
                        />
                      </v-col>
                    </v-row>
                  </v-card-text>
                </v-card>
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn
            color="grey-darken-1"
            variant="text"
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

            <v-window-item value="history">
              <VehicleHistory
                :vehiculo-id="selectedVehicle.id"
                :should-refresh="historyRefreshTrigger"
              />
            </v-window-item>

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
import {ref, computed, onMounted, watch} from 'vue';
import {useVehicleStore} from '@/stores/vehicle';
import {useAuthStore} from '@/stores/auth';
import {useSucursalService} from '@/services/SucursalService';
import VehicleHistory from '@/components/vehicles/VehicleHistory.vue';
import MaintenanceSchedule from '@/components/vehicles/MaintenceSchedule.vue';
import FallasListas from "@/components/vehicles/FallasListas.vue";

// Stores
const vehicleStore = useVehicleStore();
const authStore = useAuthStore();

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

// Constantes
const precioMinimo = 50000;
const precioMaximo = 500000;

// Estado para sucursales
const sucursales = ref([]);
const sucursalLoading = ref(false);
const sucursalError = ref(null);

const dialogs = ref({
  vehicle: false,
  report: false,
  delete: false,
  details: false
});

// Opciones
const estadosVehiculo = [
  'DISPONIBLE',
  'NO_DISPONIBLE',
  'EN_MANTENCION',
  'EN_REPARACION'
];

const opcionesOrdenamiento = [
  {title: 'Precio: Menor a Mayor', value: 'PRECIO_ASC'},
  {title: 'Precio: Mayor a Menor', value: 'PRECIO_DESC'},
  {title: 'Marca A-Z', value: 'MARCA_ASC'},
  {title: 'Marca Z-A', value: 'MARCA_DESC'}
];

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

// Headers para la tabla
const headers = [
  {title: 'Patente', key: 'patente', sortable: true},
  {title: 'Marca', key: 'marca', sortable: true},
  {title: 'Modelo', key: 'modelo', sortable: true},
  {title: 'Año', key: 'anio', sortable: true},
  {title: 'Precio', key: 'precioArriendo', sortable: true},
  {title: 'Estado', key: 'estado', sortable: true},
  {title: 'Acciones', key: 'actions', sortable: false}
];

// Filtros
const filters = ref({
  search: '',
  sucursal: null,
  estado: null,
  rangoPrecio: [precioMinimo, precioMaximo],
  ordenarPor: null
});

// Datos de formularios
const vehicleData = ref({
  marca: '',
  modelo: '',
  patente: '',
  anio: new Date().getFullYear(),
  precioArriendo: '',
  sucursalId: null,
  estado: 'DISPONIBLE',
  falla: {
    tipo: '',
    severidad: '',
    descripcion: ''
  }
});

const reportData = ref({
  descripcion: '',
  tipoFalla: '',
  severidad: ''
});

// validación
const rules = {
  required: [v => !!v || 'Este campo es requerido'],
  patente: v => /^[A-Z]{4}\d{2}$/.test(v) || 'Formato inválido (XXXX99)',
  precio: v => v > 0 || 'El precio debe ser mayor a 0',
  anio: v => {
    const currentYear = new Date().getFullYear();
    return v >= 2000 && v <= currentYear + 1 || 'Año inválido';
  }
};
const hasActiveFilters = computed(() => {
  return filters.value.search ||
    filters.value.sucursal ||
    filters.value.estado ||
    filters.value.ordenarPor ||
    filters.value.rangoPrecio[0] !== precioMinimo ||
    filters.value.rangoPrecio[1] !== precioMaximo;
});
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

const refreshHistory = () => {
  historyRefreshTrigger.value = !historyRefreshTrigger.value;
};

// Estado del snackbar que faltaba
const snackbar = ref({
  show: false,
  text: '',
  color: 'success'
});
const filteredVehicles = computed(() => {
  return vehicleStore.vehicles.filter(vehicle => {
    if (filters.value.search) {
      const search = filters.value.search.toLowerCase();
      const searchFields = [
        vehicle.patente,
        vehicle.marca,
        vehicle.modelo
      ].map(field => field.toLowerCase());

      if (!searchFields.some(field => field.includes(search))) {
        return false;
      }
    }

    // Filtro por sucursal
    if (filters.value.sucursal &&
      vehicle.sucursal?.id !== filters.value.sucursal) {
      return false;
    }

    // Filtro por estado
    if (filters.value.estado &&
      vehicle.estado !== filters.value.estado) {
      return false;
    }

    // Filtro por rango de precio
    const precio = Number(vehicle.precioArriendo);
    if (precio < filters.value.rangoPrecio[0] ||
      precio > filters.value.rangoPrecio[1]) {
      return false;
    }

    return true;
  }).sort((a, b) => {
    if (!filters.value.ordenarPor) return 0;

    switch (filters.value.ordenarPor) {
      case 'PRECIO_ASC':
        return a.precioArriendo - b.precioArriendo;
      case 'PRECIO_DESC':
        return b.precioArriendo - a.precioArriendo;
      case 'MARCA_ASC':
        return a.marca.localeCompare(b.marca);
      case 'MARCA_DESC':
        return b.marca.localeCompare(a.marca);
      default:
        return 0;
    }
  });
});

const tieneFallas = computed(() => {
  return ['EN_MANTENCION', 'EN_REPARACION'].includes(vehicleData.value.estado);
});

// metodos
const formatPrice = (price) => {
  return new Intl.NumberFormat('es-CL').format(price);
};

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

const clearFilters = () => {
  filters.value = {
    search: '',
    sucursal: null,
    estado: null,
    rangoPrecio: [precioMinimo, precioMaximo],
    ordenarPor: null
  };
};

const loadData = async () => {
  loading.value = true;
  try {
    await vehicleStore.fetchVehicles();
  } finally {
    loading.value = false;
  }
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


const openVehicleDialog = (vehicle = null) => {
  if (vehicle) {
    isEditing.value = true;
    selectedVehicle.value = vehicle;
    const sucursalId = typeof vehicle.sucursal === 'number'
      ? vehicle.sucursal
      : vehicle.sucursal?.id;

    vehicleData.value = {
      id: vehicle.id,
      marca: vehicle.marca,
      modelo: vehicle.modelo,
      patente: vehicle.patente,
      anio: vehicle.anio,
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
    isEditing.value = false;
    selectedVehicle.value = null;
    vehicleData.value = {
      marca: '',
      modelo: '',
      patente: '',
      anio: new Date().getFullYear(),
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

const saveVehicle = async () => {
  if (!vehicleForm.value?.validate()) return;

  saving.value = true;
  try {
    const vehiculoData = {
      marca: vehicleData.value.marca,
      modelo: vehicleData.value.modelo,
      patente: vehicleData.value.patente,
      anio: Number(vehicleData.value.anio),
      precioArriendo: Number(vehicleData.value.precioArriendo),
      sucursal: Number(vehicleData.value.sucursalId),
      estado: vehicleData.value.estado
    };

    let savedVehicle;
    if (isEditing.value) {
      vehiculoData.id = selectedVehicle.value.id;
      savedVehicle = await vehicleStore.updateVehicle(vehiculoData);
    } else {
      savedVehicle = await vehicleStore.createVehicle(vehiculoData);
    }

    // Si el vehiculo esta en mantenimiento o reparacion, reportar la falla
    if ((vehiculoData.estado === 'EN_MANTENCION' || vehiculoData.estado === 'EN_REPARACION')
      && vehicleData.value.falla) {
      await vehicleStore.reportarFalla({
        vehiculoId: savedVehicle.id,
        tipo: vehicleData.value.falla.tipo,
        severidad: vehicleData.value.falla.severidad,
        descripcion: vehicleData.value.falla.descripcion,
        reportadoPorId: authStore.user.id
      });
    }

    showSnackbar(
      isEditing.value ? 'Vehículo actualizado correctamente' : 'Vehículo creado correctamente',
      'success'
    );
    closeVehicleDialog();
    await loadData();
  } catch (error) {
    showSnackbar(error.message, 'error');
  } finally {
    saving.value = false;
  }
};
const showSnackbar = (text, color) => {
  snackbar.value = {
    show: true,
    text,
    color
  };
};
// Permisos y validaciones
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

onMounted(async () => {
  try {
    loading.value = true;
    await Promise.all([
      loadData(),
      loadSucursales()
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
  background: linear-gradient(to right, #1a237e, #0d47a1);
  color: white;
}

.fixed-height-input {
  margin-bottom: 0;
}

.main-table-card {
  height: calc(100vh - 300px);
  overflow: hidden;
}

.fixed-height-table {
  height: 100%;
}

:deep(.v-data-table-header th) {
  height: 48px !important;
  background-color: #f5f5f5;
}

:deep(.v-data-table-row td) {
  height: 48px !important;
  padding: 0 16px !important;
}

:deep(.v-chip) {
  min-width: 120px;
  justify-content: center;
}

:deep(.v-btn--icon) {
  width: 36px !important;
  height: 36px !important;
}

.filter-section :deep(.v-field__input) {
  color: white !important;
}

.filter-section :deep(.v-field__outline) {
  --v-field-border-opacity: 0.7;
}

.filter-section :deep(.v-label) {
  color: rgba(255, 255, 255, 0.9) !important;
}

:deep(.v-dialog) {
  border-radius: 8px;
  box-shadow: 0 4px 25px 0 rgba(0, 0, 0, 0.1);
}

:deep(.v-card-title) {
  font-size: 1.25rem;
  font-weight: 600;
}

.filter-section :deep(.v-slider-track__fill) {
  background-color: white !important;
}

.filter-section :deep(.v-slider-thumb__surface) {
  border-color: white !important;
}

@media (max-width: 960px) {
  .filter-section {
    height: auto;
  }

  .main-table-card {
    height: calc(100vh - 400px);
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
