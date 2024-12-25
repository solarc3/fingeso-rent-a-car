<template>
  <v-dialog :model-value="modelValue" @update:modelValue="updateDialog" max-width="500px">
    <v-card>
      <v-card-title>
        <span class="text-h5">Editar Usuario</span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" v-model="valid">
          <v-text-field v-model="user.nombre" label="Nombre" :rules="[rules.required]" required></v-text-field>
          <v-text-field v-model="user.apellido" label="Apellido" :rules="[rules.required]" required></v-text-field>
          <v-select
            v-model="user.rol"
            :items="roles"
            label="Rol"
            :rules="[rules.required]"
            required
          ></v-select>
          <v-checkbox
            v-model="user.esta_en_lista_negra"
            label="Está en lista negra"
          ></v-checkbox>
          <v-select
            v-if="!user.esta_en_lista_negra"
            v-model="user.estado"
            :items="estados"
            label="Estado"
            :rules="[rules.required]"
            required
          ></v-select>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn text @click="close">Cancelar</v-btn>
        <v-btn color="primary" @click="submit" :disabled="!valid">Guardar</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import { ref, watch } from 'vue';
import { useUserStore } from '@/stores/user';

export default {
  props: {
    modelValue: {
      type: Boolean,
      required: true,
    },
    user: {
      type: Object,
      required: true,
    },
  },
  emits: ['update:modelValue', 'user-updated'],
  setup(props, { emit }) {
    const userStore = useUserStore();
    const editableUser = ref({ ...props.user });
    const valid = ref(false);
    const rules = {
      required: value => !!value || 'Requerido',
    };

    const roles = ['ADMINISTRADOR', 'TRABAJADOR', 'ARRENDATARIO'];
    const estados = ['ACTIVO', 'INACTIVO'];

    watch(
      () => props.user,
      (newUser) => {
        editableUser.value = { ...newUser };
      }
    );

    const submit = async () => {
      if (valid.value) {
        try {
          await userStore.updateUser(editableUser.value.id, editableUser.value);
          emit('user-updated');
          close();
        } catch (error) {
          alert(error.message);
        }
      }
    };

    const close = () => {
      emit('update:modelValue', false);
    };

    const updateDialog = (value) => {
      emit('update:modelValue', value);
    };

    return {
      user: editableUser,
      roles,
      estados,
      valid,
      rules,
      submit,
      close,
      updateDialog,
    };
  },
};
</script>