
<template>
  <v-container>
    <v-card>
      <v-card-title class="headline">
        Login
      </v-card-title>
      <v-card-text>
        <!-- mismo q en register, datos d ingreso a la web -->
        <v-form>
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
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <!-- botones dnvo -->
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

<script>
import { ref } from 'vue';

export default {
  setup() {
    const email = ref('');
    const password = ref('');

    const login = () => {
      alert(`Email: ${email.value}, Password: ${password.value}`);
    };

    return {
      email,
      password,
      login
    };
  }
};
</script>
