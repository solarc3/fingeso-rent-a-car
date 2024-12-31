<template>
  <v-dialog
    v-model="dialog"
    max-width="500"
  >
    <v-card>
      <v-card-title class="headline primary white--text">
        Reporte de Ventas
      </v-card-title>

      <v-card-text class="mt-4">
        <v-row>
          <v-col
            cols="12"
            md="6"
          >
            <v-text-field
              v-model="fechaInicio"
              label="Fecha Inicio"
              type="date"
              :rules="dateRules"
              outlined
              dense
            />
          </v-col>

          <v-col
            cols="12"
            md="6"
          >
            <v-text-field
              v-model="fechaFin"
              label="Fecha Fin"
              type="date"
              :rules="[...dateRules, dateRangeRule]"
              outlined
              dense
            />
          </v-col>
        </v-row>

        <v-alert
          v-if="error"
          type="error"
          class="mt-2"
          dense
        >
          {{ error }}
        </v-alert>
      </v-card-text>

      <v-card-actions class="pb-4 px-6">
        <v-spacer />
        <v-btn
          color="grey darken-1"
          text
          @click="dialog = false"
        >
          Cancelar
        </v-btn>
        <v-btn
          color="primary"
          :loading="loading"
          :disabled="!isValid"
          @click="generarReporte"
        >
          Generar Reporte
          <v-icon
            right
            dark
          >
            mdi-file-pdf-box
          </v-icon>
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useReservaService } from '@/services/ReservaService';

const dialog = ref(false);
const loading = ref(false);
const error = ref('');
const fechaInicio = ref('');
const fechaFin = ref('');

// Validation rules
const dateRules = [
  v => !!v || 'La fecha es requerida',
  v => {
    const pattern = /^\d{4}-\d{2}-\d{2}$/;
    return pattern.test(v) || 'Formato de fecha inválido';
  }
];

const dateRangeRule = v => {
  if (!fechaInicio.value || !v) return true;
  return new Date(v) >= new Date(fechaInicio.value) || 'La fecha fin debe ser posterior a la fecha inicio';
};

const isValid = computed(() => {
  return fechaInicio.value &&
    fechaFin.value &&
    new Date(fechaFin.value) >= new Date(fechaInicio.value);
});

const generarReporte = async () => {
  if (!isValid.value) return;

  loading.value = true;
  error.value = '';

  try {
    const reservaService = useReservaService();
    const response = await reservaService.generarReporte(fechaInicio.value, fechaFin.value);

    // Create blob and download
    const blob = new Blob([response], { type: 'application/pdf' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `reporte-ventas-${fechaInicio.value}-${fechaFin.value}.pdf`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);

    dialog.value = false;
    // eslint-disable-next-line no-unused-vars
  } catch (e) {
    error.value = 'Error al generar el reporte';
  } finally {
    loading.value = false;
  }
};

defineExpose({
  open: () => {
    dialog.value = true;
  }
});
</script>

<style scoped>
.v-card-title {
  background: linear-gradient(to right, #002349, #1E88E5);
}
</style>
