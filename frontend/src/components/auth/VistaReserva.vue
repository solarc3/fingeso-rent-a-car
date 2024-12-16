<template>
  <div>
    <v-stepper v-model="step">
      <v-stepper-header>
        <v-stepper-step
          :complete="step > 1"
          step="1"
        >
          Arriendo
        </v-stepper-step>
        <v-divider />
        <v-stepper-step
          :complete="step > 2"
          step="2"
        >
          Login
        </v-stepper-step>
        <v-divider />
        <v-stepper-step
          :complete="step > 3"
          step="3"
        >
          Pago
        </v-stepper-step>
        <v-divider />
        <v-stepper-step
          :complete="step > 4"
          step="4"
        >
          Detalles finales
        </v-stepper-step>
      </v-stepper-header>

      <v-stepper-items>
        <v-stepper-content step="1">
          <h3>Selecciona el vehículo</h3>
          <!-- Tu contenido de selección de vehículo aquí -->
          <v-btn @click="nextStep">
            Siguiente
          </v-btn>
        </v-stepper-content>

        <v-stepper-content step="2">
          <login-form @login-success="nextStep" />
        </v-stepper-content>

        <v-stepper-content step="3">
          <h3>Proceso de pago</h3>
          <!-- Tu contenido de pago aquí -->
          <v-btn @click="nextStep">
            Siguiente
          </v-btn>
        </v-stepper-content>

        <v-stepper-content step="4">
          <h3>Detalles finales</h3>
          <!-- Tu contenido de detalles finales aquí -->
        </v-stepper-content>
      </v-stepper-items>
    </v-stepper>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import LoginForm from '@/components/auth/LoginForm.vue'

const step = ref(1)

const nextStep = () => {
  step.value++
}
</script>
