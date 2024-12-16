<template>
  <v-form @submit.prevent="handleSubmit">
    <v-container min-width="100%">
      <v-card
        class="pa-6"
        rounded="l"
      >
        <v-row>
          <!-- Filtro de marca -->
          <v-col
            cols="12"
            md="3"
          >
            <v-select
              v-model="formData.marca"
              :items="metadataStore.marcasDisponibles"
              label="Marca"
              item-title="nombre"
              item-value="valor"
              clearable
              outlined
              dense
              :loading="metadataStore.loading"
            />
          </v-col>

          <!-- Filtro de sucursal -->
          <v-col
            cols="12"
            md="3"
          >
            <v-select
              v-model="formData.sucursal"
              :items="sucursales"
              :loading="loading"
              :error-messages="error"
              label="Sucursal"
              item-title="nombre"
              item-value="id"
              clearable
              outlined
              dense
              return-object
            />
          </v-col>

          <!-- Filtro de tipo de vehículo -->
          <v-col
            cols="12"
            md="3"
          >
            <v-select
              v-model="formData.tipoVehiculo"
              :items="metadataStore.tiposVehiculo"
              label="Tipo de Vehículo"
              item-title="texto"
              item-value="valor"
              clearable
              outlined
              dense
              :loading="metadataStore.loading"
            />
          </v-col>

          <!-- Filtro de transmisión -->
          <v-col
            cols="12"
            md="3"
          >
            <v-select
              v-model="formData.transmision"
              :items="metadataStore.tiposTransmision"
              label="Transmisión"
              item-title="texto"
              item-value="valor"
              clearable
              outlined
              dense
              :loading="metadataStore.loading"
            />
          </v-col>
        </v-row>

        <v-row>
          <!-- Filtro de rango de precio -->
          <v-col
            cols="12"
            md="6"
          >
            <v-range-slider
              v-model="formData.rangoPrecio"
              :min="metadataStore.precioMinimo"
              :max="metadataStore.precioMaximo"
              :step="5000"
              label="Rango de Precio"
              thumb-label="always"
              class="mt-4"
            >
              <template #prepend>
                <v-text-field
                  v-model="formData.rangoPrecio[0]"
                  type="number"
                  outlined
                  dense
                  hide-details
                  prefix="$"
                />
              </template>
              <template #append>
                <v-text-field
                  v-model="formData.rangoPrecio[1]"
                  type="number"
                  outlined
                  dense
                  hide-details
                  prefix="$"
                />
              </template>
            </v-range-slider>
          </v-col>

          <!-- Ordenamiento -->
          <v-col
            cols="12"
            md="3"
          >
            <v-select
              v-model="formData.ordenarPor"
              :items="metadataStore.opcionesOrdenamiento"
              label="Ordenar por"
              item-title="texto"
              item-value="valor"
              outlined
              dense
            />
          </v-col>

          <!-- Botón de búsqueda -->
          <v-col
            cols="12"
            md="3"
            class="d-flex align-center justify-end"
          >
            <v-btn
              color="#be1784"
              x-large
              min-width="150"
              class="text-h6 font-weight-bold elevation-6"
              :loading="loading"
              @click="handleSubmit"
            >
              Buscar
            </v-btn>
          </v-col>
        </v-row>
      </v-card>
    </v-container>
  </v-form>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue';
import {useMetadataStore} from '@/stores/metadata';
import {useSucursalService} from '@/services/SucursalService';

const emit = defineEmits(['submit']);
const metadataStore = useMetadataStore();

// Estado
const loading = ref(false);
const error = ref(null);
const sucursales = ref([]);

// Datos del formulario
const formData = reactive({
  marca: null,
  sucursal: null,
  tipoVehiculo: null,
  transmision: null,
  rangoPrecio: [metadataStore.precioMinimo, metadataStore.precioMaximo],
  ordenarPor: metadataStore.opcionesOrdenamiento[0]
});

// Métodos
const loadSucursales = async () => {
  loading.value = true;
  error.value = null;

  try {
    const sucursalService = useSucursalService();
    sucursales.value = await sucursalService.listarSucursales();
    console.log('Sucursales cargadas:', sucursales.value);
  } catch (err) {
    error.value = 'Error al cargar sucursales';
    console.error('Error loading sucursales:', err);
  } finally {
    loading.value = false;
  }
};

const handleSubmit = () => {
  const searchData = {
    marca: formData.marca,
    sucursal: formData.sucursal,
    tipoVehiculo: formData.tipoVehiculo,
    transmision: formData.transmision,
    precioMinimo: Number(formData.rangoPrecio[0]),
    precioMaximo: Number(formData.rangoPrecio[1]),
    ordenarPor: formData.ordenarPor
  };

  console.log('Submitting search:', searchData);
  emit('submit', searchData);
};
// Lifecycle hooks
onMounted(async () => {
  console.log('Buscador mounted');
  await Promise.all([
    loadSucursales(),
    metadataStore.loadMetadata()
  ]);
  console.log('Metadata loaded:', {
    marcas: metadataStore.marcasDisponibles,
    tipos: metadataStore.tiposVehiculo,
    transmisiones: metadataStore.tiposTransmision
  });
});
</script>

<style scoped>
.v-card {
  background-color: rgba(255, 255, 255, 0.9);
}

.v-btn {
  text-transform: none;
}
</style>
