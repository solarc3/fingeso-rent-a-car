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

    <v-main style="background-color: white;">
      <Buscador />
      <v-container
        width="100%"
        min-width="100%"
      >
        <div class="text-center">
          <v-pagination
            v-model="currentPage"
            :length="numberOfPages"
            :total-visible="7"
          />
        </div>
        <v-row style="padding: 10px">
          <v-col
            v-for="n in displayedItems"
            :key="n"
            cols="12"
            sm="6"
            md="2"
            lg="3"
            style="padding: 10px"
          >
            <VehiculoCard />
          </v-col>
        </v-row>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup>
import {ref, computed} from 'vue';
import AppBar from "@/components/AppBar.vue";
import LoginMenu from "@/components/Login.vue";
import RegisterMenu from "@/components/Register.vue";
import Buscador from "@/components/Buscador.vue";
import VehiculoCard from "@/components/VehiculoCard.vue";

const showLoginMenu = ref(false);
const showRegisterMenu = ref(false);

//paginacion
const itemsPerPage = 12;
const totalItems = 48;
const currentPage = ref(1);


const numberOfPages = computed(() => {
  return Math.ceil(totalItems / itemsPerPage);
});

const displayedItems = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = Math.min(start + itemsPerPage, totalItems);
  return end - start;
});

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
