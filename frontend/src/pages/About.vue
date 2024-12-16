<template>
  <!-- Contenido Principal -->
  <v-container
    fluid
    class="pa-0"
  >
    <v-row
      no-gutters
      style="min-height: 100vh;"
    >
      <v-col
        cols="12"
        md="6"
        class="d-flex align-center justify-center pa-10"
        style="background-color: #d1e4ff"
      >
        <div>
          <h1
            class="text-h2 font-weight-bold"
            style="color: #333;"
          >
            Sobre nosotros
          </h1>
          <p
            class="text-subtitle-1 mt-4"
            style="color: #555;"
          >
            Somos una...
          </p>
        </div>
      </v-col>

      <v-img
        src="../assets/about.png"
        max-width="800"
        class="mx-auto mb-4"
      />
    </v-row>

    <v-footer
      color="#002349"
      padless
    />
  </v-container>
</template>

<script>

</script>

<style scoped>
h1 {
  line-height: 1.2;
}


p {
  font-size: 1.1rem;
  line-height: 1.8;
}

</style>



