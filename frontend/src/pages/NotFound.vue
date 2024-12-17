<template>
  <v-container
    fluid
    class="d-flex flex-column align-center justify-center"
    style="min-height: 80vh;"
  >
    <v-card
      max-width="600"
      class="text-center pa-6"
    >
      <h1 class="text-h3 mb-6">
        404 - Página no encontrada
      </h1>

      <v-img
        src="@/assets/raton.gif"
        max-width="300"
        class="mx-auto mb-6"
      />

      <p class="text-body-1 mb-6">
        Lo sentimos, la página que buscas no existe o ha sido movida.
      </p>

      <v-btn
        color="primary"
        size="large"
        to="/"
      >
        Volver al inicio
      </v-btn>
    </v-card>
  </v-container>
</template>

<script setup>
</script>

<style scoped>
.v-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
}
</style>
