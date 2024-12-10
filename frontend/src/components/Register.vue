
<template>
  <v-card>
    <v-card-title class="headline">
      Register
    </v-card-title>
    <v-card-text>
      <!-- datos varios para regustrarse eb la web -->
      <v-form>
        <v-text-field
          v-model="nombre"
          label="Nombre"
          required
        />
        <v-text-field
          v-model="apellidos"
          label="Apellidos"
          required
        />
        <v-text-field
          v-model="rut"
          label="RUT"
          required
        />
        <v-text-field
          v-model="telefono"
          label="Teléfono"
          required
        />
        <v-text-field
          v-model="email"
          label="Correo"
          required
        />
        <v-text-field
          v-model="password"
          label="Contraseña"
          type="password"
          required
        />
        <v-text-field
          v-model="confirmPassword"
          label="Confirmar Contraseña"
          type="password"
          required
        />
      </v-form>
    </v-card-text>
    <v-card-actions>
      <v-spacer />
      <!-- only botones lol -->
      <v-btn
        color="blue darken-1"
        text
        @click="close"
      >
        Cancelar
      </v-btn>
      <v-btn
        color="blue darken-1"
        text
        @click="register"
      >
        Registrarse
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>
import { ref, watch } from 'vue';

export default {
  props: ['visible'],
  setup(props, { emit }) {
    const nombre = ref('');
    const apellidos = ref('');
    const rut = ref('');
    const telefono = ref('');
    const email = ref('');
    const password = ref('');
    const confirmPassword = ref('');
    const isVisible = ref(props.visible);

    watch(() => props.visible, (newVal) => {
      isVisible.value = newVal;
    });

    const close = () => {
      isVisible.value = false;
      emit('close');
    };

    const register = () => {
      if (password.value !== confirmPassword.value) {
        alert("Las contraseñas no coinciden");
        return;
      }
      close();
    };

    return {
      nombre,
      apellidos,
      rut,
      telefono,
      email,
      password,
      confirmPassword,
      isVisible,
      close,
      register
    };
  }
};
</script>
