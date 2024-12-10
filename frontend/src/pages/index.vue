<template>
  <v-layout>
    <v-app-bar
      title=""
      color="var(--primary-a0)"
    >
      <div class="title-container">
        <a
          class="title-link"
          @click="goToHome"
        >
          Rent a Car
          <img
            src="@/assets/auto.png"
            alt="Car"
            class="car-img"
          >
        </a>
      </div>
      <v-spacer />
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
    </v-app-bar>
    <v-dialog
      v-model="showLoginMenu"
      max-width="500px"
    >
      <LoginMenu @close="closeLoginMenu" />
    </v-dialog>

    <v-dialog
      v-model="showRegisterMenu"
      max-width="500px"
    >
      <RegisterMenu @close="closeRegisterMenu" />
    </v-dialog>
    <v-main>
      <v-container class="mt-12">
        <Buscador />
      </v-container>
    </v-main>
  </v-layout>
</template>
<script setup>
import {ref} from 'vue';
import Buscador from "@/components/Buscador.vue";
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

const closeLoginMenu = () => {
  showLoginMenu.value = false;
};

const closeRegisterMenu = () => {
  showRegisterMenu.value = false;
};

const goToHome = () => {
  showLoginMenu.value = false;
  showRegisterMenu.value = false;
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
