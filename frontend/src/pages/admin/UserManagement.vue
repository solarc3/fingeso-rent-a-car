<template>
  <v-container fluid>
    <v-row class="mb-4 header-row">
      <v-col
        cols="12"
        class="d-flex align-center"
      >
        <h2 class="text-h4">
          Gestión de Usuarios
        </h2>
        <v-spacer />
        <v-btn
          color="primary"
          prepend-icon="mdi-plus"
          @click="openCreateUserDialog"
        >
          Crear Usuario
        </v-btn>
      </v-col>
    </v-row>

    <v-card class="mb-4 filter-section">
      <v-card-text>
        <v-row>
          <v-col
            cols="12"
            md="4"
          >
            <v-text-field
              v-model="filters.search"
              label="Buscar por nombre/RUT"
              prepend-inner-icon="mdi-magnify"
              clearable
              hide-details
              class="fixed-height-input"
            />
          </v-col>

          <v-col
            cols="12"
            md="4"
          >
            <v-select
              v-model="filters.rol"
              :items="roles"
              label="Rol"
              clearable
              hide-details
              class="fixed-height-input"
            />
          </v-col>

          <v-col
            cols="12"
            md="4"
          >
            <v-select
              v-model="filters.estado"
              :items="[
                { text: 'Todos', value: 'TODOS' },
                { text: 'Activos', value: 'ACTIVO' },
                { text: 'Lista Negra', value: 'LISTA_NEGRA' }
              ]"
              label="Estado"
              clearable
              hide-details
              class="fixed-height-input"
            >
              <template #selection="{ item }">
                <v-chip
                  :color="item.raw.value === 'LISTA_NEGRA' ? 'error' : 'success'"
                  size="small"
                  class="mr-2"
                >
                  {{ item.raw.text }}
                </v-chip>
              </template>
            </v-select>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <v-card class="main-table-card">
      <v-data-table
        :headers="headers"
        :items="filteredUsers"
        :loading="loading"
        :items-per-page="10"
        class="fixed-height-table"
        height="calc(100vh - 300px)"
        fixed-header
      >
        <template #item.nombre="{ item }">
          {{ `${item.nombre} ${item.apellido}` }}
        </template>

        <template #item.rol="{ item }">
          <v-chip
            :color="getRolColor(item.rol)"
            size="small"
          >
            {{ item.rol }}
          </v-chip>
        </template>

        <template #item.estado="{ item }">
          <v-chip
            :color="item.estaEnListaNegra ? 'error' : 'success'"
            size="small"
          >
            {{ item.estaEnListaNegra ? 'Lista Negra' : 'Activo' }}
          </v-chip>
        </template>

        <template #item.actions="{ item }">
          <v-tooltip text="Editar">
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                icon="mdi-pencil"
                size="small"
                class="mr-2"
                @click="openEditUserDialog(item)"
              />
            </template>
          </v-tooltip>

          <v-tooltip text="Eliminar">
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                icon="mdi-delete"
                size="small"
                color="error"
                @click="confirmDelete(item)"
              />
            </template>
          </v-tooltip>
        </template>
      </v-data-table>
    </v-card>

    <CreateUserDialog
      v-model="dialogs.create"
      @user-created="fetchUsers"
    />

    <EditUserDialog
      v-model="dialogs.edit"
      :user="selectedUser"
      @user-updated="fetchUsers"
    />

    <v-dialog
      v-model="dialogs.delete"
      max-width="400"
    >
      <v-card>
        <v-card-title>Confirmar Eliminación</v-card-title>
        <v-card-text>
          ¿Está seguro que desea eliminar este usuario?
          Esta acción no se puede deshacer.
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            color="grey"
            text
            @click="dialogs.delete = false"
          >
            Cancelar
          </v-btn>
          <v-btn
            color="error"
            :loading="loading"
            @click="deleteUser"
          >
            Eliminar
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      :timeout="6000"
    >
      {{ snackbar.text }}
      <template #actions>
        <v-btn
          color="white"
          variant="text"
          @click="snackbar.show = false"
        >
          Cerrar
        </v-btn>
      </template>
    </v-snackbar>
  </v-container>

  <!-- Delete confirmation dialog -->
  <v-dialog
    v-model="dialogs.delete"
    max-width="400"
  >
    <v-card>
      <v-card-title>Confirmar Eliminación</v-card-title>
      <v-card-text>
        ¿Está seguro que desea eliminar al usuario
        {{ selectedUser?.nombre }} {{ selectedUser?.apellido }}?
        Esta acción no se puede deshacer.
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn
          color="grey"
          text
          @click="dialogs.delete = false"
        >
          Cancelar
        </v-btn>
        <v-btn
          color="error"
          :loading="saving"
          @click="deleteUser"
        >
          Eliminar
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import {ref, computed, onMounted} from 'vue';
import {useUserStore} from '@/stores/user';
import CreateUserDialog from '@/components/admin/CreateUserDialog.vue';
import EditUserDialog from '@/components/admin/EditUserDialog.vue';
import saving from "lodash/seq.js";

const userStore = useUserStore();
const loading = ref(false);
const selectedUser = ref(null);

const headers = [
  {title: 'Nombre', key: 'nombre', sortable: true},
  {title: 'RUT', key: 'rut', sortable: true},
  {title: 'Rol', key: 'rol', sortable: true},
  {title: 'Estado', key: 'estado', sortable: true},
  {title: 'Acciones', key: 'actions', sortable: false},
];

const roles = ['ADMINISTRADOR', 'TRABAJADOR', 'ARRENDATARIO'];
const estados = ['ACTIVO', 'INACTIVO', 'LISTA_NEGRA'];

const filters = ref({
  search: '',
  rol: null,
  estado: null,
});

const dialogs = ref({
  create: false,
  edit: false,
  delete: false,
});

const snackbar = ref({
  show: false,
  text: '',
  color: 'success',
});

const filteredUsers = computed(() => {
  return userStore.users.filter(user => {
    if (filters.value.search) {
      const search = filters.value.search.toLowerCase();
      const searchFields = [
        user.nombre,
        user.apellido,
        user.rut,
      ].map(field => field?.toLowerCase());

      if (!searchFields.some(field => field?.includes(search))) {
        return false;
      }
    }

    if (filters.value.rol && user.rol !== filters.value.rol) {
      return false;
    }

    if (filters.value.estado) {
      if (filters.value.estado === 'LISTA_NEGRA') {
        if (!user.estaEnListaNegra) return false;
      } else if (filters.value.estado === 'ACTIVO') {
        if (user.estaEnListaNegra) return false;
      }
    }

    return true;
  });
});

const getRolColor = (rol) => {
  const colors = {
    'ADMINISTRADOR': 'purple',
    'TRABAJADOR': 'blue',
    'ARRENDATARIO': 'green',
  };
  return colors[rol] || 'grey';
};

const getStatusColor = (estado) => {
  const colors = {
    'ACTIVO': 'success',
    'INACTIVO': 'error',
    'LISTA_NEGRA': 'red-darken-4',
  };
  return colors[estado] || 'grey';
};

const openCreateUserDialog = () => {
  dialogs.value.create = true;
};

const openEditUserDialog = (user) => {
  selectedUser.value = user;
  dialogs.value.edit = true;
};

const confirmDelete = (user) => {
  selectedUser.value = user;
  dialogs.value.delete = true;
};

const deleteUser = async () => {
  saving.value = true;
  try {
    await userStore.deleteUser(selectedUser.value.id);

    dialogs.value.delete = false;
    selectedUser.value = null;

    snackbar.value = {
      show: true,
      color: 'success',
      text: 'Usuario eliminado correctamente'
    };
  } catch (error) {
    snackbar.value = {
      show: true,
      color: 'error',
      text: error.message || 'Error al eliminar usuario'
    };
  } finally {
    saving.value = false;
  }
};

const fetchUsers = async () => {
  loading.value = true;
  try {
    await userStore.fetchUsers();
  } catch (error) {
    snackbar.value = {
      show: true,
      color: 'error',
      text: error.message || 'Error al cargar usuarios',
    };
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchUsers();
});
</script>

<style scoped>
.header-row {
  height: 64px;
}

.filter-section {
  background: linear-gradient(to right, #1a237e, #0d47a1);
  color: white;
}

.fixed-height-input {
  margin-bottom: 0;
}

.main-table-card {
  height: calc(100vh - 300px);
  overflow: hidden;
}

.fixed-height-table {
  height: 100%;
}

:deep(.v-data-table-header th) {
  height: 48px !important;
  background-color: #f5f5f5;
}

:deep(.v-data-table-row td) {
  height: 48px !important;
  padding: 0 16px !important;
}

:deep(.v-chip) {
  min-width: 100px;
  justify-content: center;
}

.filter-section :deep(.v-field__input) {
  color: white !important;
}

.filter-section :deep(.v-field__outline) {
  --v-field-border-opacity: 0.7;
}

.filter-section :deep(.v-label) {
  color: rgba(255, 255, 255, 0.9) !important;
}
</style>
