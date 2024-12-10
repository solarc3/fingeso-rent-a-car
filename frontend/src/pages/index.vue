
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
        </a>
        <img
          src="@/assets/auto.png"
          alt="Car"
          class="car-img"
        >
      </div>
      <v-spacer />
      <!-- Botones de Login y Registro -->
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
  </v-layout>
  <v-container class="mt-12">
    <!-- condicion para mostrar el buscador solito -->
    <Buscador v-if="!showLoginMenu && !showRegisterMenu" />
    <!-- caso contrario mostramos o el registro o el login -->
    <LoginMenu
      v-if="showLoginMenu"
      @close="closeLoginMenu"
    />
    <RegisterMenu
      v-if="showRegisterMenu"
      @close="closeRegisterMenu"
    />
  </v-container>
</template>

<script setup>
import { ref } from 'vue';
import Buscador from "@/components/Buscador.vue";
import LoginMenu from "@/components/Login.vue";
import RegisterMenu from "@/components/Register.vue";

const showLoginMenu = ref(false);
const showRegisterMenu = ref(false);

// literal mostrar el formulario de correo y contraseña para login
const showLoginForm = () => {
  showLoginMenu.value = true;
  showRegisterMenu.value = false;
};

// lo mismo pero con registro que tiene mas cosas
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

<!-- config textos e imagenes en gnral -->
<style scoped>
.title-container {
  display: flex;
  align-items: center;
  margin-left: 20px; /* se m habia descuadrao el rent a car xddd */
}

.title-link {
  cursor: pointer;
  color: inherit;
  text-decoration: none;
  margin-right: 10px;
}

.car-img {
  height: 40px;
  object-fit: contain;
}

/* ES LA D A IMAGEN Q M PITIE CREO
.img {
  display: flex;
  object-fit: contain;
  height: 40px;
  padding-right: 15px;
  filter: grayscale(1);
}
 */

</style>
