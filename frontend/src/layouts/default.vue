<template>
  <div>
    <app-bar
      @show-login="showLoginForm"
      @show-register="showRegisterForm"
      @go-home="goToHome"
    />

    <v-dialog
      v-model="showLoginMenu"
      max-width="500px"
      persistent
    >
      <login-form
        @login-success="handleLoginSuccess"
        @login-error="handleLoginError"
        @close="closeLoginDialog"
      />
    </v-dialog>

    <v-dialog
      v-model="showRegisterMenu"
      max-width="500px"
    >
      <register-form />
    </v-dialog>

    <v-main>
      <router-view />
      <!-- app-footer --->
    </v-main>
    <v-snackbar
      v-model="showSnackbar"
      :color="snackbarColor"
      timeout="3000"
    >
      {{ snackbarText }}
    </v-snackbar>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import {useAuth} from '@/composables/useAuth';
import AppBar from '@/components/layout/AppBar.vue';
import AppFooter from '@/components/layout/AppFooter.vue';
import LoginForm from '@/components/auth/LoginForm.vue';
import RegisterForm from '@/components/auth/RegisterForm.vue';

const {
  showLoginMenu,
  showRegisterMenu,
  showLoginForm,
  showRegisterForm,
  goToHome
} = useAuth();

// snackbar state
const showSnackbar = ref(false);
const snackbarText = ref('');
const snackbarColor = ref('success');

// login event
const handleLoginSuccess = (userData) => {
  showLoginMenu.value = false; // Cerrar el diálogo
  snackbarColor.value = 'success';
  snackbarText.value = `Bienvenido ${userData.nombre}!`;
  showSnackbar.value = true;
};
// login error event
const handleLoginError = (errorMessage) => {
  snackbarColor.value = 'error';
  snackbarText.value = errorMessage;
  showSnackbar.value = true;
};
// close login dialog
const closeLoginDialog = () => {
  showLoginMenu.value = false;
};
</script>
