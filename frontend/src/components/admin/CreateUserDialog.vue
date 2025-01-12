<!-- filepath: /home/felipe/Desktop/Universidad/Sexto Semestre/Fundamentos de Ingeniería de Software/Hito 4/rent-a-car/frontend/src/components/admin/CreateUserDialog.vue -->
<template>
  <v-dialog
    :model-value="modelValue"
    max-width="500px"
    @update:model-value="updateDialog"
  >
    <v-card>
      <v-card-title>
        <span class="text-h5">Crear Usuario</span>
      </v-card-title>
      <v-card-text>
        <v-form
          ref="form"
          v-model="valid"
        >
          <v-text-field
            v-model="user.rut"
            label="RUT"
            :rules="[rules.required]"
            required
          />
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
          <v-text-field
            v-model="user.password"
            label="Contraseña"
            type="password"
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
          <v-select
            v-model="user.sucursalId"
            :items="sucursales"
            label="Sucursal"
            item-title="nombre"
            item-value="id"
            :rules="[rules.required]"
            required
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
          Crear
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useUserStore } from '@/stores/user';
import { useSucursalStore } from '@/stores/sucursal';
import {useSucursalService} from "@/services/SucursalService.js";

export default {
  props: {
    modelValue: {
      type: Boolean,
      required: true,
    },
  },
  emits: ['update:modelValue', 'user-created'],
  setup(props, { emit }) {
    const userStore = useUserStore();
    const sucursalService = useSucursalService();
    const user = ref({
      rut: '',
      nombre: '',
      apellido: '',
      password: '',
      rol: '',
      sucursalId: null,
    });
    const valid = ref(false);
    const rules = {
      required: value => !!value || 'Requerido',
    };

    const roles = ['ADMINISTRADOR', 'TRABAJADOR', 'ARRENDATARIO'];
    const sucursales = ref([]);

    onMounted(async () => {
      try {
        const response = await sucursalService.listarSucursales();
        // Transform the data to match the v-select requirements
        sucursales.value = response.map(sucursal => ({
          id: sucursal.id,
          nombre: sucursal.nombre
        }));
      } catch (error) {
        console.error('Error cargando sucursales:', error);
      }
    });

    const submit = async () => {
      if (valid.value) {
        try {
          await userStore.createUser(user.value);
          emit('user-created');
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
      user,
      roles,
      sucursales,
      valid,
      rules,
      submit,
      close,
      updateDialog,
    };
  },
};
</script>
