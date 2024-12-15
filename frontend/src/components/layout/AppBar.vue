<template>
  <v-app-bar
    title=""
    color="#002349"
  >
    <div class="title-container">
      <a
        class="title-link"
        @click="goToHome"
      >
        Rent a Car
        <img
          src="../../assets/auto.png"
          alt="Car"
          class="car-img"
        >
      </a>
    </div>
    <v-spacer />
    <!-- Usuario NO autenticado -->
    <template v-if="!authStore.isAuthenticated">
      <v-btn to="/about">
        About
      </v-btn>
      <v-btn
        outlined
        @click="showLoginForm"
      >
        Login
      </v-btn>
      <v-btn
        outlined
        @click="showRegisterForm"
      >
        Register
      </v-btn>
    </template>
    <!-- Usuario logged -->
    <template v-else>
      <!-- Menú para Administrador -->
      <template v-if="authStore.isAdmin">
        <v-btn to="/admin/usuarios">
          Gestionar Usuarios
        </v-btn>
        <v-btn to="/admin/vehiculos">
          Gestionar Vehículos
        </v-btn>
      </template>

      <!-- Menú para Trabajador -->
      <template v-if="authStore.isWorker">
        <v-btn to="/trabajador/reservas">
          Gestionar Reservas
        </v-btn>
      </template>

      <!-- Menú de usuario -->
      <v-menu>
        <template #activator="{ props }">
          <v-btn v-bind="props">
            <v-icon>mdi-account</v-icon>
            {{ authStore.user.nombre }}
          </v-btn>
        </template>

        <v-list>
          <v-list-item>
            <v-list-item-title>
              {{ authStore.user.nombre }} {{ authStore.user.apellido }}
            </v-list-item-title>
            <v-list-item-subtitle>
              {{ authStore.user.rol }}
            </v-list-item-subtitle>
          </v-list-item>

          <v-divider />

          <v-list-item to="/perfil">
            <v-list-item-title>Mi Perfil</v-list-item-title>
          </v-list-item>

          <v-list-item to="/mis-reservas">
            <v-list-item-title>Mis Reservas</v-list-item-title>
          </v-list-item>

          <v-list-item @click="handleLogout">
            <v-list-item-title class="text-error">
              Cerrar Sesión
            </v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </template>
  </v-app-bar>
</template>

<script setup>

import {useAuthStore} from '@/stores/auth'
import {useRouter} from 'vue-router'

const router = useRouter()
const authStore = useAuthStore()
const emit = defineEmits(['showLogin', 'showRegister', 'goHome']);

const showLoginForm = () => {
  emit('showLogin');
};

const showRegisterForm = () => {
  emit('showRegister');
};

const goToHome = () => {
  emit('goHome');
  router.push('/')
};
</script>

<style scoped>
.title-container {
  display: flex;
  align-items: center;
  margin-left: 20px;
}

.title-link {
  cursor: pointer;
  color: inherit;
  text-decoration: none;
  margin-right: 10px;
  display: flex;
  align-items: center;
}

.car-img {
  height: 40px;
  object-fit: contain;
  margin-left: 10px;
}
</style>
