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
      <register-form
        @register-success="handleRegisterSuccess"
        @register-error="handleRegisterError"
        @close="closeRegisterDialog"
      />
    </v-dialog>

    <v-main>
      <router-view />
      <!-- app-footer --->
    </v-main>
    <v-snackbar
      v-model="showSnackbar"
      :color="snackbarColor"
      :timeout="3000"
      @update:model-value="handleSnackbarUpdate"
    >
      {{ snackbarText }}
      <template #actions>
        <v-btn
          color="white"
          text
          @click="closeSnackbar"
        >
          Cerrar
        </v-btn>
      </template>
    </v-snackbar>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import {useAuth} from '@/composables/useAuth';
import RegisterForm from "@/components/auth/RegisterForm.vue";
import LoginForm from "@/components/auth/LoginForm.vue";
import AppBar from "@/components/layout/AppBar.vue";

const showSnackbar = ref(false);
const snackbarText = ref('');
const snackbarColor = ref('success');

const {
  showLoginMenu,
  showRegisterMenu,
  showLoginForm,
  showRegisterForm,
  goToHome
} = useAuth();

const closeLoginDialog = () => {
  showLoginMenu.value = false;
};

const handleLoginSuccess = (userData) => {
  closeLoginDialog();
  snackbarColor.value = 'success';
  snackbarText.value = `Bienvenido ${userData.nombre}!`;
  showSnackbar.value = true;
};

const handleLoginError = (errorMessage) => {
  snackbarColor.value = 'error';
  snackbarText.value = errorMessage;
  showSnackbar.value = true;
};

const closeSnackbar = () => {
  showSnackbar.value = false;
};

const handleSnackbarUpdate = (value) => {
  if (!value) {
    showSnackbar.value = false;
  }
};
const closeRegisterDialog = () => {
  showRegisterMenu.value = false;
};
const handleRegisterSuccess = (userData) => {
  closeRegisterDialog();
  snackbarColor.value = 'success';
  snackbarText.value = `¡Bienvenido ${userData.nombre}! Registro exitoso.`;
  showSnackbar.value = true;
};

const handleRegisterError = (errorMessage) => {
  snackbarColor.value = 'error';
  snackbarText.value = errorMessage;
  showSnackbar.value = true;
};
</script>
