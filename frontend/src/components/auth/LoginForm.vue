<template>
  <v-container>
    <v-card>
      <v-card-title class="headline">
        Login
      </v-card-title>
      <v-card-text>
        <v-form>
          <v-text-field
            v-model="rut"
            label="RUT"
            required
          />
          <v-text-field
            v-model="password"
            label="Contraseña"
            type="password"
            required
          />
          <v-select
            v-model="rol"
            :items="roles"
            label="Tipo de Usuario"
            required
          />
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          text
          @click="login"
        >
          Iniciar sesión
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-container>
</template>

<script setup>
import {ref} from 'vue';
import {useAuthStore} from '@/stores/auth';
import {useRouter} from 'vue-router';

const router = useRouter();
const authStore = useAuthStore();

const rut = ref('');
const password = ref('');
const rol = ref('');
const roles = ['ARRENDATARIO', 'TRABAJADOR', 'ADMINISTRADOR'];

const login = async () => {
  try {
    if (!rut.value || !password.value || !rol.value) {
      alert('Por favor complete todos los campos');
      return;
    }
    await authStore.login(rut.value, password.value, rol.value);
    await router.push('/');
  } catch (error) {
    alert('Error al iniciar sesión: ' + error.message);
  }
};
</script>
