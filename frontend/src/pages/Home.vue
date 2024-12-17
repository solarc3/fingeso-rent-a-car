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
import Buscador from "@/components/Buscador.vue";
import VehiculoCard from "@/components/vehicles/VehiculoCard.vue";

const router = useRouter();
const vehicleStore = useVehicleStore();

// Estado básico
const currentPage = ref(1);
const itemsPerPage = 12;

// Computed properties
const loading = computed(() => vehicleStore.loading);

const numberOfPages = computed(() => {
  return Math.ceil(vehiculos.value.length / itemsPerPage);
});

const paginatedVehiculos = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return vehiculos.value.slice(start, end);
});

// Métodos

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

// Lifecycle hooks
onMounted(async () => {
  await vehicleStore.fetchVehicles();
});
const vehiculos = computed(() => {
  return vehicleStore.getFilteredVehicles;
});
const handleSearch = (searchData) => {
  console.log('Handling search with data:', searchData);
  vehicleStore.setFilters(searchData);
  currentPage.value = 1;
};
</script>

<style scoped>
.v-alert {
  margin-bottom: 16px;
}
</style>
