

<template>
  <v-form>
    <v-container>
      <v-card
        outlined
        class="pa-4"
      >
        <v-row>
          <v-col cols="12">
            <v-row>
              <v-col
                cols="12"
                md="3"
              >
                <v-select
                  v-model="sucursal"
                  label="Sucursal de retiro"
                  :items="sucursales"
                  outlined
                  dense
                  required
                />
              </v-col>

              <v-col
                cols="12"
                md="3"
              >
                <v-menu
                  ref="menu1"
                  v-model="menu1"
                  :close-on-content-click="false"
                  :nudge-right="40"
                  transition="scale-transition"
                  offset-y
                  min-width="290px"
                >
                  <template #activator="{ on, attrs }">
                    <v-text-field
                      v-model="fechaRetiro"
                      label="Fecha de retiro"
                      prepend-icon="mdi-calendar"
                      readonly
                      v-bind="attrs"
                      v-on="on"
                    />
                  </template>
                  <v-date-picker
                    v-model="fechaRetiro"
                    @input="menu1 = false"
                  />
                </v-menu>
              </v-col>

              <v-col
                cols="12"
                md="3"
              >
                <v-menu 
                  ref="menu2"
                  v-model="menu2"
                  :close-on-content-click="false"
                  :nudge-right="40"
                  transition="scale-transition"
                  offset-y
                  min-width="290px"
                >
                  <template #activator="{ on, attrs }">
                    <v-text-field
                      v-model="fechaDevolucion"
                      label="Fecha de devolución"
                      prepend-icon="mdi-calendar"
                      readonly
                      v-bind="attrs"
                      v-on="on"
                    />
                  </template>
                  <v-date-picker
                    v-model="fechaDevolucion"
                    @input="menu2 = false"
                  />
                </v-menu>
              </v-col>

              <v-col
                cols="12"
                md="3"
                class="text-center"
              >
                <v-btn
                  color="primary"
                  @click="modificarReserva"
                >
                  Modificar Reserva
                </v-btn>
              </v-col>
            </v-row>
          </v-col>
        </v-row>
      </v-card>
    </v-container>
  </v-form>
</template>

<script>
export default {
  data() {
    return {
      sucursal: '',
      fechaRetiro: '',
      fechaDevolucion: '',
      menu1: false,
      menu2: false,
      sucursales: ['Sucursal Iquique', 'Sucursal Santiago', 'Sucursal ...']
    };
  },
  methods: {
    modificarReserva() {
      alert(`Reserva modificada para la sucursal: ${this.sucursal}, Fecha de retiro: ${this.fechaRetiro}, Fecha de devolución: ${this.fechaDevolucion}`);
    }
  }
};
</script>

<style scoped>
</style>
