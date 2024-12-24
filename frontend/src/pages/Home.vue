<template>
  <v-container
    fluid
    style="background-color: white;"
  >
    <Buscador
      :fechas-actuales="fechasSeleccionadas"
      :vehiculos="vehiculos"
      @submit="handleSearch"
    />
    <v-container
      width="100%"
      min-width="100%"
    >
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
        <div class="text-center">
          <v-pagination
            v-model="currentPage"
            :length="numberOfPages"
            :total-visible="7"
          />
        </div>

        <v-row style="padding: 10px">
          <v-col
            v-for="vehiculo in paginatedVehiculos"
            :key="vehiculo.id"
            cols="12"
            sm="6"
            md="4"
            lg="3"
          >
            <VehiculoCard
              :vehiculo="vehiculo"
              :fechas-seleccionadas="fechasSeleccionadas"
              @seleccionar="seleccionarVehiculo"
            />
          </v-col>
        </v-row>

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
import {ref, computed, onMounted, watch} from 'vue';
import {useRouter} from 'vue-router';
import {useVehicleStore} from '@/stores/vehicle';
import Buscador from "@/components/Buscador.vue";
import VehiculoCard from "@/components/vehicles/VehiculoCard.vue";
import {useSearchStore} from "@/stores/search.js";
import {useMetadataStore} from "@/stores/metadata.js";

const router = useRouter();
const vehicleStore = useVehicleStore();
const searchStore = useSearchStore();
const metadataStore = useMetadataStore();

const currentPage = ref(1);
const itemsPerPage = 12;
const vehiculos = computed(() => vehicleStore.getFilteredVehicles);
const loading = computed(() => vehicleStore.loading);

const numberOfPages = computed(() => {
  return Math.ceil(vehiculos.value.length / itemsPerPage);
});

const paginatedVehiculos = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return vehiculos.value.slice(start, end);
});

const fechasSeleccionadas = ref({
  inicio: null,
  fin: null
});

const handleSearch = (searchData) => {
  try {
    fechasSeleccionadas.value = {
      inicio: searchData.fechas.inicio,
      fin: searchData.fechas.fin
    };
    currentPage.value = 1;
    // La actualización de los vehículos filtrados se manejará automáticamente
    // a través del watcher en vehicleStore
  } catch (error) {
    searchStore.setGlobalError('Error al aplicar filtros');
    console.error('Error applying filters:', error);
  }
};
const seleccionarVehiculo = (vehiculo) => {
  router.push({
    path: '/payment',
    query: {
      vehiculoId: vehiculo.id,
      sucursal: vehiculo.sucursal?.id,
      precioArriendo: vehiculo.precioArriendo
    }
  });
};
watch(
  () => searchStore.globalError,
  (error) => {
    if (error) {
      // Manejar el error global aquí
      console.error('Global error:', error);
    }
  }
);
watch(
  () => vehicleStore.vehicles,
  (newVehicles) => {
    if (newVehicles.length > 0) {
      console.log('Vehicles updated, applying filters');
      handleSearch({
        ...searchStore.filters,
        precioMinimo: searchStore.filters.rangoPrecio[0],
        precioMaximo: searchStore.filters.rangoPrecio[1]
      });
    }
  },
  {immediate: true}
);
onMounted(async () => {
  try {
    searchStore.setGlobalLoadingState(true);

    // Cargar datos iniciales
    await Promise.all([
      metadataStore.loadMetadata(),
      searchStore.loadSucursales(),
      vehicleStore.fetchVehicles()
    ]);

    // Inicializar filtros y configurar watchers
    searchStore.initializeFilters();
    searchStore.initializeFechas();
    vehicleStore.setupFilterWatchers(); // Agregar esta línea

    // Aplicar filtros iniciales
    handleSearch({
      ...searchStore.filters,
      precioMinimo: searchStore.filters.rangoPrecio[0],
      precioMaximo: searchStore.filters.rangoPrecio[1]
    });
  } catch (error) {
    searchStore.setGlobalError('Error al cargar datos iniciales');
    console.error('Error in onMounted:', error);
  } finally {
    searchStore.setGlobalLoadingState(false);
  }
});


</script>

<style scoped>
.v-alert {
  margin-bottom: 16px;
}
</style>
