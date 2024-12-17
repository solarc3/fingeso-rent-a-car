<template>
  <v-card>
    <v-card-title class="headline">
      Register
    </v-card-title>
    <v-card-text>
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
        @submit.prevent="handleRegister"
      >
        <v-text-field
          v-model="nombre"
          label="Nombre"
          :rules="[v => !!v || 'Nombre es requerido']"
          required
        />
        <v-text-field
          v-model="apellidos"
          label="Apellidos"
          :rules="[v => !!v || 'Apellidos es requerido']"
          required
        />
        <rut-input
          v-model="rut"
          required
          @validation="handleRutValidation"
        />
        <v-text-field
          v-model="telefono"
          label="Teléfono"
          :rules="telefonoRules"
          required
        />
        <v-text-field
          v-model="email"
          label="Correo"
          :rules="emailRules"
          required
        />
        <v-text-field
          v-model="password"
          label="Contraseña"
          type="password"
          :rules="passwordRules"
          required
        />
        <v-text-field
          v-model="confirmPassword"
          label="Confirmar Contraseña"
          type="password"
          :rules="confirmPasswordRules"
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
        :disabled="!isFormValid"
        @click="handleRegister"
      >
        Registrarse
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script setup>
import {ref} from 'vue';
import {useAuthStore} from '@/stores/auth';
import {useRouter} from 'vue-router';
import RutInput from "@/components/auth/RutInput.vue";
import {useUsuarioService} from "@/services/UsuarioService.js";

const router = useRouter();
const authStore = useAuthStore();
const emit = defineEmits(['close', 'register-success', 'register-error']);

// referencias y estado
const form = ref(null);
const isFormValid = ref(false);
const loading = ref(false);
const error = ref('');
const isRutValid = ref(false);

// formulario
const nombre = ref('');
const apellidos = ref('');
const rut = ref('');
const telefono = ref('');
const email = ref('');
const password = ref('');
const confirmPassword = ref('');

// validacion
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const phoneRegex = /^[+]?[(]?[0-9]{1,4}[)]?[-\s.]?[0-9]{3}[-\s.]?[0-9]{4,6}$/;

const emailRules = [
  v => !!v || 'Email es requerido',
  v => emailRegex.test(v) || 'Email invalido'
];

const telefonoRules = [
  v => !!v || 'Telefono es requerido',
  v => phoneRegex.test(v) || 'Telefono invalido'
];

const passwordRules = [
  v => !!v || 'Contraseña es requerida',
];

const confirmPasswordRules = [
  v => !!v || 'Confirmar contraseña es requerida',
  v => v === password.value || 'Las contraseñas no coinciden'
];

// methods
const handleRutValidation = (isValid) => {
  isRutValid.value = isValid;
};

const validateForm = () => {
  return form.value?.validate() && isRutValid.value;
};

const handleRegister = async () => {
  if (!validateForm()) return;

  loading.value = true;
  error.value = '';

  try {
    const usuarioData = {
      rut: rut.value,
      nombre: nombre.value.trim(),
      apellido: apellidos.value.trim(),
      password: password.value,
      rol: 'ARRENDATARIO'
    };

    // registrar usuario
    const nuevoUsuario = await useUsuarioService().crearUsuario(usuarioData);

    // login automatico despues del registro
    const usuarioLogueado = await authStore.login(
      nuevoUsuario.rut,
      password.value,
      'ARRENDATARIO'
    );

    emit('register-success', usuarioLogueado);
    emit('close');

    await router.push('/');
  } catch (err) {
    error.value = err.message || 'Error al registrar usuario';
    emit('register-error', error.value);
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
