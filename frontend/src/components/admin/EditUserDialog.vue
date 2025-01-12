<template>
  <v-dialog
    :model-value="modelValue"
    max-width="500px"
    @update:model-value="updateDialog"
  >
    <v-card>
      <v-card-title>
        <span class="text-h5">Editar Usuario</span>
      </v-card-title>
      <v-card-text>
        <v-form
          ref="form"
          v-model="valid"
        >
          <v-text-field
            v-model="user.nombre"
            label="Nombre"
            :rules="[rules.required]"
            required
          />

          <v-text-field
            v-model="user.apellido"
            label="Apellido"
            :rules="[rules.required]"
            required
          />

          <v-select
            v-model="user.rol"
            :items="roles"
            label="Rol"
            :rules="[rules.required]"
            required
          />

          <v-checkbox
            v-model="user.estaEnListaNegra"
            label="Agregar a lista negra"
            :hint="user.estaEnListaNegra ? 'Usuario en lista negra' : 'Usuario activo'"
            persistent-hint
          />
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn
          text
          @click="close"
        >
          Cancelar
        </v-btn>
        <v-btn
          color="primary"
          :disabled="!valid"
          @click="submit"
        >
          Guardar
        </v-btn>
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
      required: v => !!v || 'Este campo es requerido',
    };

    const roles = ['ADMINISTRADOR', 'TRABAJADOR', 'ARRENDATARIO'];

    watch(
      () => props.user,
      (newUser) => {
        editableUser.value = { ...newUser };
      }
    );

    const submit = async () => {
      if (valid.value) {
        try {
          await userStore.updateUser(editableUser.value.id, {
            nombre: editableUser.value.nombre,
            apellido: editableUser.value.apellido,
            rol: editableUser.value.rol,
            estaEnListaNegra: editableUser.value.estaEnListaNegra
          });
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
      valid,
      rules,
      submit,
      close,
      updateDialog,
    };
  },
};
</script>
