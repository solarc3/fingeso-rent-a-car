<template>
  <v-container
    fluid
    style="background-color: white;"
  >
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
  </v-container>
</template>

<script setup>
import {ref, computed} from 'vue';
import Buscador from "@/components/Buscador.vue";
import VehiculoCard from "@/components/VehiculoCard.vue";

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
</script>
