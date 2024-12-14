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
      Nombre Vehiculo
    </v-card-title>
    <v-card-subtitle>
      Detalles
    </v-card-subtitle>
    <v-card-subtitle>
      Mas detalles aca
    </v-card-subtitle>

    <v-card-actions>
      <v-btn
        color="var(--primary-a20)"
        text="ARRENDAR"
        class="text-white"
        variant="elevated"
        to="/payment"
      />
    </v-card-actions>
  </v-card>
</template>
<script setup>
</script>
