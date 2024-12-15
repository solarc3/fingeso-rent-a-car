<template>
  <v-app>
    <router-view />
  </v-app>
</template>

<script setup>
import {onMounted} from 'vue'
import {useAuthStore} from '@/stores/auth'

const authStore = useAuthStore()

onMounted(() => {
  // recover session si es que hay alguna logeada
  authStore.initializeAuth()
})
</script>
