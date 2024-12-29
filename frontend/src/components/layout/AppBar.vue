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
      <!-- Menu admin -->
      <template v-if="authStore.isAdmin">
        <v-btn
          to="/admin/usuarios"
          class="mx-2"
        >
          Gestionar Usuarios
        </v-btn>
        <v-btn
          to="/admin/vehiculos"
          class="mx-2"
        >
          Gestionar Vehículos
        </v-btn>

        <v-btn
          class="mx-2"
          @click="openReport"
        >
          <v-icon start>
            mdi-file-chart
          </v-icon>
          Reporte de Ventas
        </v-btn>

        <SalesReport ref="reportDialog" />
      </template>

      <!-- Menu trabajador -->
      <template v-if="authStore.isWorker">
        <v-btn
          to="/trabajador/reservas"
          class="mx-2"
        >
          Gestionar Reservas
        </v-btn>
      </template>

      <!-- Menu usuario -->
      <v-menu
        location="bottom end"
        transition="slide-y-transition"
      >
        <template #activator="{ props }">
          <v-btn
            v-bind="props"
            class="mx-2"
            color="primary"
          >
            <v-icon start>
              mdi-account
            </v-icon>
            {{ authStore.user.nombre }}
            <v-icon end>
              mdi-chevron-down
            </v-icon>
          </v-btn>
        </template>

        <v-card
          min-width="200"
          elevation="4"
        >
          <v-list>
            <v-list-item>
              <template #prepend>
                <v-avatar
                  color="primary"
                  class="mr-3"
                >
                  <v-icon>mdi-account</v-icon>
                </v-avatar>
              </template>
              <v-list-item-title class="font-weight-bold">
                {{ authStore.user.nombre }} {{ authStore.user.apellido }}
              </v-list-item-title>
              <v-list-item-subtitle>
                {{ authStore.user.rol }}
              </v-list-item-subtitle>
            </v-list-item>

            <v-divider />

            <v-list-item
              to="/perfil"
              prepend-icon="mdi-account-details"
            >
              <v-list-item-title>Mi Perfil</v-list-item-title>
            </v-list-item>

            <v-list-item
              to="/mis-reservas"
              prepend-icon="mdi-calendar-check"
            >
              <v-list-item-title>Mis Reservas</v-list-item-title>
            </v-list-item>

            <v-divider />

            <v-list-item
              prepend-icon="mdi-logout"
              color="error"
              @click="confirmLogout"
            >
              <v-list-item-title>Cerrar Sesión</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-card>
      </v-menu>
    </template>

    <!-- dialogo confirmacion logoujt -->
    <v-dialog
      v-model="showLogoutDialog"
      max-width="400"
    >
      <v-card>
        <v-card-title class="text-h5">
          Confirmar cierre de sesión
        </v-card-title>

        <v-card-text>
          ¿Esta seguro que desea cerrar la sesion?
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn
            color="grey"
            text
            @click="showLogoutDialog = false"
          >
            Cancelar
          </v-btn>
          <v-btn
            color="error"
            @click="handleLogout"
          >
            Cerrar Sesion
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Snackbar para mensajes -->
    <v-snackbar
      v-model="showSnackbar"
      :color="snackbarColor"
      timeout="3000"
    >
      {{ snackbarMessage }}

      <template #actions>
        <v-btn
          color="white"
          text
          @click="showSnackbar = false"
        >
          Cerrar
        </v-btn>
      </template>
    </v-snackbar>
  </v-app-bar>
</template>
<script setup>
import {ref} from 'vue';
import SalesReport from '@/components/admin/SalesReport.vue';
import {useAuthStore} from '@/stores/auth';
import {useRouter} from 'vue-router';

const router = useRouter();
const authStore = useAuthStore();
const emit = defineEmits(['showLogin', 'showRegister', 'goHome']);

// state para dialogos, snackbar y logout
const showLogoutDialog = ref(false);
const showSnackbar = ref(false);
const snackbarMessage = ref('');
const snackbarColor = ref('success');

// metodos navegacion
const showLoginForm = () => {
  emit('showLogin');
};

const showRegisterForm = () => {
  emit('showRegister');
};

const goToHome = () => {
  emit('goHome');
  router.push('/');
};

// metodos logout
const confirmLogout = () => {
  showLogoutDialog.value = true;
};

const handleLogout = async () => {
  try {
    authStore.logout();
    showLogoutDialog.value = false;
    showSnackbar.value = true;
    snackbarColor.value = 'success';
    snackbarMessage.value = 'Sesión cerrada correctamente';
    await router.push('/');
  } catch (error) {
    console.error('Error during logout:', error);
    snackbarColor.value = 'error';
    snackbarMessage.value = 'Error al cerrar sesión';
    showSnackbar.value = true;
  }
};

const reportDialog = ref(null);

const openReport = () => {
  reportDialog.value?.open();
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
