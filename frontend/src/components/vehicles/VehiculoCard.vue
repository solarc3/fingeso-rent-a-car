<template>
  <v-card
    i
    class="mx-auto"
    min-width="450px"
    rounded="l"
    elevation="8"
    ripple
  >
    <v-img
      min-width="450px"
      min-height="200px"
      src="../../assets/Autocard.png"
      cover
    />

    <v-card-title>
      {{ vehiculo.marca }} {{ vehiculo.modelo }}
    </v-card-title>
    <v-card-subtitle>
      Patente: {{ vehiculo.patente }}
    </v-card-subtitle>
    <v-card-subtitle>
      Precio: ${{ vehiculo.precioArriendo }} / día
    </v-card-subtitle>

    <v-card-actions>
      <v-btn
        color="#be1784"
        text="ARRENDAR"
        class="text-white"
        variant="elevated"
        to="/payment"
        :disabled="vehiculo.estado !== 'DISPONIBLE'"
        @click="seleccionarVehiculo"
      />
    </v-card-actions>
  </v-card>
</template>
<script setup>
const props = defineProps({vehiculo: {type: Object, required: true}})
const emit = defineEmits(['seleccionar'])
const seleccionarVehiculo = () => {
  emit('seleccionar', props.vehiculo)
}
</script>
