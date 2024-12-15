<template>
  <v-container
    fluid
    style="background-color: white;"
  >
    <Buscador />
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
      </div>
    </v-container>
  </v-container>
</template>

<script setup>
import {ref, computed, onMounted} from 'vue';
import {useRouter} from 'vue-router';
import Buscador from "@/components/Buscador.vue";
import VehiculoCard from "@/components/vehicles/VehiculoCard.vue";
import {useVehiculoService} from '@/services/VehiculoService';

const router = useRouter();
const vehiculoService = useVehiculoService();

const vehiculos = ref([]);
const loading = ref(true);
const currentPage = ref(1);
const itemsPerPage = 12;

// Obtener vehículos del backend
const fetchVehiculos = async () => {
  try {
    loading.value = true;
    vehiculos.value = await vehiculoService.obtenerVehiculos();
  } catch (error) {
    console.error('Error al obtener vehículos:', error);
  } finally {
    loading.value = false;
  }
};

// Calcular número de páginas
const numberOfPages = computed(() => {
  return Math.ceil(vehiculos.value.length / itemsPerPage);
});

// Obtener vehículos paginados
const paginatedVehiculos = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return vehiculos.value.slice(start, end);
});

// Manejar selección de vehículo
const seleccionarVehiculo = (vehiculo) => {
  // Aquí puedes manejar la selección del vehículo
  // Por ejemplo, guardar en el store o navegar a la página de detalles
  router.push({
    path: '/payment',
    query: {vehiculoId: vehiculo.id}
  });
};

// Cargar vehículos al montar el componente
onMounted(() => {
  fetchVehiculos();
});
</script>
