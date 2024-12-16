<template>
  <v-container
    fluid
    style="background-color: white;"
  >
    <Buscador @submit="handleSearch" />
    <v-container
      width="100%"
      min-width="100%"
    >
      <!-- Loading state -->
      <div
        v-if="loading"
        class="d-flex justify-center"
      >
        <v-progress-circular
          indeterminate
          color="primary"
        />
      </div>

      <div v-else>
        <!-- Filtros activos -->
        <v-alert
          v-if="filtrosActivos"
          type="info"
          class="mb-4"
          border="start"
          elevation="2"
        >
          <div class="d-flex align-center flex-wrap">
            <span class="mr-2 text-subtitle-2">Filtros aplicados:</span>

            <v-chip
              v-if="filtros.marca"
              class="ma-1"
              color="primary"
              closable
              variant="elevated"
              @click:close="limpiarFiltroMarca"
            >
              <v-icon start>
                mdi-car-side
              </v-icon>
              Marca: {{ getMarcaNombre(filtros.marca) }}
            </v-chip>

            <v-chip
              v-if="filtros.sucursal"
              class="ma-1"
              color="primary"
              closable
              variant="elevated"
              @click:close="limpiarFiltroSucursal"
            >
              <v-icon start>
                mdi-office-building-marker
              </v-icon>
              Sucursal: {{ filtros.sucursal.nombre }}
            </v-chip>

            <v-chip
              v-if="filtros.tipoVehiculo"
              class="ma-1"
              color="secondary"
              closable
              variant="elevated"
              @click:close="limpiarFiltroTipo"
            >
              <v-icon start>
                mdi-car-info
              </v-icon>
              Tipo: {{ getTipoVehiculoNombre(filtros.tipoVehiculo) }}
            </v-chip>

            <v-chip
              v-if="filtros.transmision"
              class="ma-1"
              color="info"
              closable
              variant="elevated"
              @click:close="limpiarFiltroTransmision"
            >
              <v-icon start>
                mdi-car-shift-pattern
              </v-icon>
              Transmisión: {{ getTransmisionNombre(filtros.transmision) }}
            </v-chip>

            <v-chip
              v-if="tieneFiltroPrecio"
              class="ma-1"
              color="success"
              closable
              variant="elevated"
              @click:close="limpiarFiltroPrecio"
            >
              <v-icon start>
                mdi-cash
              </v-icon>
              Precio: ${{ filtros.precioMinimo }} - ${{ filtros.precioMaximo }}
            </v-chip>

            <v-btn
              color="error"
              variant="text"
              size="small"
              class="ml-auto"
              @click="limpiarFiltros"
            >
              Limpiar todos
              <v-icon end>
                mdi-close-circle
              </v-icon>
            </v-btn>
          </div>
        </v-alert>

        <!-- Paginación -->
        <div class="text-center">
          <v-pagination
            v-model="currentPage"
            :length="numberOfPages"
            :total-visible="7"
          />
        </div>

        <!-- Grid de vehículos -->
        <v-row style="padding: 10px">
          <v-col
            v-for="vehiculo in paginatedVehiculos"
            :key="vehiculo.id"
            cols="12"
            sm="6"
            md="2"
            lg="3"
            style="padding: 10px"
          >
            <VehiculoCard
              :vehiculo="vehiculo"
              @seleccionar="seleccionarVehiculo"
            />
          </v-col>
        </v-row>

        <!-- Mensaje sin resultados -->
        <v-alert
          v-if="!paginatedVehiculos.length"
          type="warning"
          class="mt-4"
        >
          No se encontraron vehículos con los criterios seleccionados
        </v-alert>
      </div>
    </v-container>
  </v-container>
</template>
<script setup>
import {ref, computed, onMounted} from 'vue';
import {useRouter} from 'vue-router';
import {useVehicleStore} from '@/stores/vehicle';
import {useMetadataStore} from '@/stores/metadata'; // Añadir esta importación
import Buscador from "@/components/Buscador.vue";
import VehiculoCard from "@/components/vehicles/VehiculoCard.vue";

// Setup
const router = useRouter();
const vehicleStore = useVehicleStore();
const metadataStore = useMetadataStore(); // Añadir esta línea

// Estado básico
const currentPage = ref(1);
const itemsPerPage = 12;

// Computed properties
const loading = computed(() => vehicleStore.loading);
const vehiculos = computed(() => vehicleStore.vehicles);
const vehiculosFiltrados = computed(() => vehicleStore.filteredVehicles);
const filtros = computed(() => vehicleStore.filters);
const filtrosActivos = computed(() => {
  const f = vehicleStore.filters;
  return !!(f.marca || f.sucursal || f.tipoVehiculo || f.transmision ||
    (f.precioMinimo !== metadataStore.precioMinimo) ||
    (f.precioMaximo !== metadataStore.precioMaximo));
});

// Helpers para mostrar nombres en los chips de filtros
const getMarcaNombre = (marca) => metadataStore.getMarcaNombre(marca);
const getTipoVehiculoNombre = (tipo) => metadataStore.getTipoVehiculoNombre(tipo);
const getTransmisionNombre = (transmision) => metadataStore.getTransmisionNombre(transmision);

const tieneFiltroPrecio = computed(() => {
  return filtros.value.precioMinimo !== metadataStore.precioMinimo ||
    filtros.value.precioMaximo !== metadataStore.precioMaximo;
});

const numberOfPages = computed(() => {
  const listaVehiculos = filtrosActivos.value ? vehiculosFiltrados.value : vehiculos.value;
  return Math.ceil(listaVehiculos.length / itemsPerPage);
});

const paginatedVehiculos = computed(() => {
  const listaVehiculos = filtrosActivos.value ? vehiculosFiltrados.value : vehiculos.value;
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return listaVehiculos.slice(start, end);
});

// Métodos simplificados
const handleSearch = (searchData) => {
  console.log('Received search data:', searchData);
  vehicleStore.setFilters(searchData);
  currentPage.value = 1;
};

const limpiarFiltros = () => {
  vehicleStore.clearFilters();
  currentPage.value = 1;
};

const limpiarFiltroMarca = () => vehicleStore.updateFilter('marca', null);
const limpiarFiltroSucursal = () => vehicleStore.updateFilter('sucursal', null);
const limpiarFiltroTipo = () => vehicleStore.updateFilter('tipoVehiculo', null);
const limpiarFiltroTransmision = () => vehicleStore.updateFilter('transmision', null);
const limpiarFiltroPrecio = () => vehicleStore.resetPriceFilter();

// Navegación
const seleccionarVehiculo = (vehiculo) => {
  router.push({
    path: '/payment',
    query: {
      vehiculoId: vehiculo.id,
      sucursal: filtros.value.sucursal?.id,
      precioArriendo: vehiculo.precioArriendo
    }
  });
};

// Lifecycle hooks
onMounted(async () => {
  await Promise.all([
    vehicleStore.fetchVehicles(),
    metadataStore.loadMetadata()
  ]);
});
</script>
<style scoped>
.v-alert {
  margin-bottom: 16px;
}
</style>
