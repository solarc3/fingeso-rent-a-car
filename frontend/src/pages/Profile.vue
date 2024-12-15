<template>
  <v-container>
    <v-card
      class="mx-auto"
      max-width="800"
    >
      <v-card-title class="text-h5">
        Mi Perfil
      </v-card-title>

      <v-card-text>
        <v-row>
          <v-col
            cols="12"
            sm="6"
          >
            <strong>Nombre:</strong> {{ authStore.user.nombre }}
          </v-col>
          <v-col
            cols="12"
            sm="6"
          >
            <strong>Apellido:</strong> {{ authStore.user.apellido }}
          </v-col>
          <v-col
            cols="12"
            sm="6"
          >
            <strong>RUT:</strong> {{ authStore.user.rut }}
          </v-col>
          <v-col
            cols="12"
            sm="6"
          >
            <strong>Rol:</strong> {{ authStore.user.rol }}
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import {useAuthStore} from '@/stores/auth'

const authStore = useAuthStore()
</script>
