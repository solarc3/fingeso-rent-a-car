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

        <v-form
          ref="form"
          v-model="isFormValid"
          @submit.prevent="handleLogin"
        >
          <rut-input
            v-model="rut"
            required
            @validation="handleRutValidation"
          />

          <v-text-field
            v-model="password"
            label="Contraseña"
            type="password"
            :rules="passwordRules"
            required
          />

          <v-select
            v-model="rol"
            :items="roles"
            label="Tipo de Usuario"
            :rules="rolRules"
            required
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
          :disabled="!isFormValid || !isRutValid"
          @click="handleLogin"
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
import RutInput from "@/components/auth/RutInput.vue";

const router = useRouter();
const authStore = useAuthStore();

// Props y Emits
const emit = defineEmits(['login-success', 'login-error', 'close']);

// Referencias y estado
const form = ref(null);
const isFormValid = ref(false);
const isRutValid = ref(false);
const loading = ref(false);
const error = ref('');

// Estado del formulario
const rut = ref('');
const password = ref('');
const rol = ref('');
const roles = ['ARRENDATARIO', 'TRABAJADOR', 'ADMINISTRADOR'];

// validate
const passwordRules = [
  v => !!v || 'Contraseña es requerida',
];

const rolRules = [
  v => !!v || 'Seleccione un rol'
];

// metodos
const handleRutValidation = (isValid) => {
  isRutValid.value = isValid;
};

const validateForm = () => {
  return form.value?.validate() && isRutValid.value;
};

const handleLogin = async () => {
  if (!validateForm()) return;

  loading.value = true;
  error.value = '';

  try {
    const result = await authStore.login(
      rut.value,
      password.value,
      rol.value
    );

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
  if (form.value) {
    form.value.reset();
  }
  emit('close');
};
</script>
