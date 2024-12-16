<template>
  <v-form @submit.prevent="buscarVehiculos">
    <v-container min-width="100%">
      <v-card
        class="pa-6"
        rounded="l"
      >
        <div class="d-flex">
          <v-card class="pa-4 flex-grow-1 mr-4">
            <v-row>
              <v-col
                cols="12"
                md="4"
              >
                <label class="text-subtitle-2 font-weight-bold mb-2">Sucursal de retiro</label>
                <v-select
                  v-model="sucursal"
                  :items="sucursales"
                  outlined
                  dense
                  required
                  placeholder="Selecciona tu sucursal"
                />
              </v-col>

              <v-col
                cols="12"
                md="4"
              >
                <label class="text-subtitle-2 font-weight-bold mb-2">Fecha de retiro</label>
                <div class="d-flex">
                  <v-text-field
                    v-model="fechaRetiro"
                    type="date"
                    outlined
                    dense
                    class="mr-2 flex-grow-1"
                    :rules="[v => !!v || 'Fecha de retiro requerida']"
                  />
                  <v-select
                    v-model="horaRetiro"
                    :items="horasDisponibles"
                    outlined
                    dense
                    style="max-width: 120px"
                    :rules="[v => !!v || 'Hora de retiro requerida']"
                  />
                </div>
              </v-col>

              <v-col
                cols="12"
                md="4"
              >
                <label class="text-subtitle-2 font-weight-bold mb-2">Fecha de devolución</label>
                <div class="d-flex">
                  <v-text-field
                    v-model="fechaDevolucion"
                    type="date"
                    outlined
                    dense
                    class="mr-2 flex-grow-1"
                    :rules="[v => !!v || 'Fecha de devolución requerida']"
                  />
                  <v-select
                    v-model="horaDevolucion"
                    :items="horasDisponibles"
                    outlined
                    dense
                    style="max-width: 120px"
                    :rules="[v => !!v || 'Hora de devolución requerida']"
                  />
                </div>
              </v-col>
            </v-row>

            <v-row class="mt-4">
              <v-col
                cols="12"
                md="6"
              >
                <v-checkbox
                  v-model="otraSucursal"
                  label="Quiero devolver en otra sucursal"
                  color="success"
                  hide-details
                />
              </v-col>
              <v-col
                v-if="otraSucursal"
                cols="12"
                md="6"
              >
                <label class="text-subtitle-2 font-weight-bold mb-2">Sucursal de devolución</label>
                <v-select
                  v-model="sucursalDevolucion"
                  :items="sucursales"
                  outlined
                  dense
                  required
                  placeholder="Selecciona tu sucursal de devolución"
                />
              </v-col>
            </v-row>
          </v-card>
          <div class="d-flex align-center">
            <v-btn
              color="grey"
              x-large
              height="64"
              min-width="150"
              class="text-h6 font-weight-bold elevation-6"
              type="submit"
            >
              Buscar
            </v-btn>
          </div>
        </div>
      </v-card>
    </v-container>
  </v-form>
</template>

<script setup>
import { ref, computed } from 'vue';
import axios from 'axios';
import { useVehicleStore } from '@/stores/vehicle';

// Initialize the vehicle store
const vehicleStore = useVehicleStore();

// Define the selectedDates ref
const selectedDates = ref({
  start: '2024-12-21T10:00:00',
  end: '2024-12-23T10:00:00'
});

// Define other necessary refs
const sucursal = ref('');
const sucursalDevolucion = ref('');
const fechaRetiro = ref('');
const fechaDevolucion = ref('');
const horaRetiro = ref('');
const horaDevolucion = ref('');
const otraSucursal = ref(false);

const sucursales = ref([
  'Sucursal Iquique',
  'Sucursal Santiago',
  'Sucursal Antofagasta'
]);

const horasDisponibles = ref([
  '08:30', '09:00', '09:30', '10:00', '10:30',
  '11:00', '11:30', '12:00', '12:30', '13:00',
  '13:30', '14:00', '14:30', '15:00', '15:30',
  '16:00', '16:30', '17:00', '17:30'
]);

// Function to check if dates overlap
const hasDateOverlap = (start1, end1, start2, end2) => {
  return (start1 < end2) && (start2 < end1);
};

// Function to check if a vehicle is available in the selected date range
const isVehicleAvailable = (vehicle, start, end) => {
  return !vehicle.reservas?.some(reserva => {
    return hasDateOverlap(
      reserva.fechaInicio,
      reserva.fechaFin,
      start,
      end
    );
  });
};

// Computed property to filter vehicles based on availability and other criteria
computed(() => {
  return vehicleStore.vehicles.value.filter(vehicle => {
    // Verificar disponibilidad en fechas seleccionadas
    if (selectedDates.value) {
      const { start, end } = selectedDates.value;
      if (!isVehicleAvailable(vehicle, start, end)) return false;
    }
    return true;
  });
});
// Function to handle vehicle search
const buscarVehiculos = async () => {
  if (!sucursal.value || !fechaRetiro.value || !horaRetiro.value || !fechaDevolucion.value || !horaDevolucion.value || (otraSucursal.value && !sucursalDevolucion.value)) {
    alert("Todos los campos son obligatorios.");
    return;
  }

  try {
    const response = await axios.post('/api/verificarDisponibilidad', {
      sucursal: sucursal.value,
      fechaRetiro: `${fechaRetiro.value}T${horaRetiro.value}:00`,
      fechaDevolucion: `${fechaDevolucion.value}T${horaDevolucion.value}:00`,
      otraSucursal: otraSucursal.value,
      sucursalDevolucion: sucursalDevolucion.value
    });

    if (response.data.disponible) {
      alert("Vehículos disponibles.");
    } else {
      alert("No hay vehículos disponibles para las fechas seleccionadas.");
    }
  } catch (error) {
    console.error("Error al verificar disponibilidad:", error);
    alert("Ocurrió un error al verificar la disponibilidad.");
  }
};
</script>

<style scoped>
.v-text-field >>> .v-input__slot {
  min-height: 40px !important;
}
</style>
