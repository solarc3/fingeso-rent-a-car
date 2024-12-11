<template>
  <v-app>
    <AppBar
      @show-login="showLoginForm"
      @show-register="showRegisterForm"
      @go-home="goToHome"
    />
    <v-dialog
      v-model="showLoginMenu"
      max-width="500px"
    >
      <LoginMenu />
    </v-dialog>

    <v-dialog
      v-model="showRegisterMenu"
      max-width="500px"
    >
      <RegisterMenu />
    </v-dialog>

    <v-main>
      <router-view />
    </v-main>
  </v-app>
</template>

<script setup>
import {ref} from 'vue';
import AppBar from "@/components/AppBar.vue";
import LoginMenu from "@/components/Login.vue";
import RegisterMenu from "@/components/Register.vue";

const showLoginMenu = ref(false);
const showRegisterMenu = ref(false);

const showLoginForm = () => {
  showLoginMenu.value = true;
  showRegisterMenu.value = false;
};

const showRegisterForm = () => {
  showRegisterMenu.value = true;
  showLoginMenu.value = false;
};

const goToHome = () => {
  showLoginMenu.value = false;
  showRegisterMenu.value = false;
};
</script>
