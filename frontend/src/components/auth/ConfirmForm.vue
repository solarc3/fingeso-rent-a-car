<template>
  <v-container fluid>
    <v-row justify="center">
      <v-col
        cols="12"
        md="8"
      >
        <v-card
          class="pa-6"
          rounded="l"
        >
          <v-card-title class="text-h5 mb-4">
            Detalles Finales del Arriendo
          </v-card-title>
          <v-form>
            <v-text-field
              v-model="address"
              label="Dirección"
              required
            />
            <v-text-field
              v-model="phone"
              label="Número de Contacto"
              required
            />
            <v-text-field
              v-model="retriever"
              label="Quién Retira"
              required
            />
            <v-text-field
              v-model="returner"
              label="Quién Devuelve"
              required
            />
            <v-btn
              color="primary"
              @click="confirmDetails"
            >
              Confirmar
            </v-btn>
          </v-form>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>

const confirmDetails = () => {
  alert('Detalles confirmados')
}
</script>

