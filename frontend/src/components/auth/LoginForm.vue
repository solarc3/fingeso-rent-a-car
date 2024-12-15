<template>
  <v-container>
    <v-card>
      <v-card-title class="headline">
        Login
      </v-card-title>
      <v-card-text>
        <!-- Alerta de error -->
        <v-alert
          v-if="error"
          type="error"
          class="mb-4"
          closable
        >
          {{ error }}
        </v-alert>

        <v-form @submit.prevent="handleLogin">
          <v-text-field
            v-model="rut"
            label="RUT"
            required
            :error-messages="rutError ? 'RUT es requerido' : []"
          />
          <v-text-field
            v-model="password"
            label="Contraseña"
            type="password"
            required
            :error-messages="passwordError ? 'Contraseña es requerida' : []"
          />
          <v-select
            v-model="rol"
            :items="roles"
            label="Tipo de Usuario"
            required
            :error-messages="rolError ? 'Seleccione un rol' : []"
          />
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn
          color="grey"
          text
          @click="handleCancel"
        >
          Cancelar
        </v-btn>
        <v-btn
          color="primary"
          :loading="loading"
          @click="handleLogin"
        >
          Iniciar sesión
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-container>
</template>

<script setup>
import {ref, computed} from 'vue';
import {useAuthStore} from '@/stores/auth';
import {useRouter} from 'vue-router';

const router = useRouter();
const authStore = useAuthStore();

// Props y Emits
const emit = defineEmits(['login-success', 'login-error', 'close']);

// Estado del formulario
const rut = ref('');
const password = ref('');
const rol = ref('');
const roles = ['ARRENDATARIO', 'TRABAJADOR', 'ADMINISTRADOR'];
const error = ref('');
const loading = ref(false);

// validaciones
const rutError = computed(() => rut.value.length === 0);
const passwordError = computed(() => password.value.length === 0);
const rolError = computed(() => !rol.value);

// metodos
const validateForm = () => {
  error.value = '';

  if (!rut.value || !password.value || !rol.value) {
    error.value = 'Por favor complete todos los campos';
    return false;
  }
  return true;
};

const handleLogin = async () => {
  if (!validateForm()) return;

  loading.value = true;
  error.value = '';

  try {
    const result = await authStore.login(rut.value, password.value, rol.value);
    emit('login-success', result);
    emit('close');
    await router.push('/');
  } catch (err) {
    error.value = err.message || 'Error al iniciar sesion';
    emit('login-error', error.value);
  } finally {
    loading.value = false;
  }
};
const handleCancel = () => {
  emit('close');
};
</script>
