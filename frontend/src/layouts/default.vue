<template>
  <v-app>
    <v-main>
      <AppFooter />
      <router-view />
    </v-main>
  </v-app>
</template>

<script setup>
import AppFooter from "@/components/AppFooter.vue";
</script>
