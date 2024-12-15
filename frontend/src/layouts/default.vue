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
    >
      <login-form />
    </v-dialog>

    <v-dialog
      v-model="showRegisterMenu"
      max-width="500px"
    >
      <register-form />
    </v-dialog>

    <v-main>
      <router-view />
      <app-footer />
    </v-main>
  </div>
</template>

<script setup>
import {useAuth} from '@/composables/useAuth'
import AppBar from '@/components/layout/AppBar.vue'
import AppFooter from '@/components/layout/AppFooter.vue'
import LoginForm from '@/components/auth/LoginForm.vue'
import RegisterForm from '@/components/auth/RegisterForm.vue'

const {
  showLoginMenu,
  showRegisterMenu,
  showLoginForm,
  showRegisterForm,
  goToHome
} = useAuth()
</script>
