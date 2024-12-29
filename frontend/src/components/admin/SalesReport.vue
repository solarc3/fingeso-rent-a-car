<template>
  <v-dialog
    v-model="dialog"
    max-width="800"
  >
    <v-card>
      <v-card-title>
        Reporte de Ventas
      </v-card-title>

      <v-card-text>
        <v-row>
          <v-col
            cols="12"
            md="6"
          >
            <v-menu>
              <template #activator="{ props }">
                <v-text-field
                  v-model="fechaInicio"
                  label="Fecha Inicio"
                  type="date"
                  v-bind="props"
                />
              </template>
            </v-menu>
          </v-col>

          <v-col
            cols="12"
            md="6"
          >
            <v-menu>
              <template #activator="{ props }">
                <v-text-field
                  v-model="fechaFin"
                  label="Fecha Fin"
                  type="date"
                  v-bind="props"
                />
              </template>
            </v-menu>
          </v-col>
        </v-row>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="primary"
          :loading="loading"
          @click="generarReporte"
        >
          Generar Reporte PDF
        </v-btn>
        <v-btn
          text
          @click="dialog = false"
        >
          Cerrar
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { ref } from 'vue';
import { useReservaService } from '@/services/ReservaService';

const dialog = ref(false);
const loading = ref(false);
const fechaInicio = ref('');
const fechaFin = ref('');

const generarReporte = async () => {
  loading.value = true;
  try {
    const reservaService = useReservaService();
    const response = await reservaService.generarReporte(fechaInicio.value, fechaFin.value);

    // Crear blob y descargar PDF
    const blob = new Blob([response], { type: 'application/pdf' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'reporte-ventas.pdf';
    link.click();

    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error('Error generando reporte:', error);
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
