<template>
  <v-form>
    <v-container>
      <v-col>
        <v-card
          outlined
          class="pa-6"
          rounded="xl"
          style="background-color: var(--mixed-a100);"
        >
          <div class="d-flex">
            <v-card class="pa-4 flex-grow-1 mr-4">
              <v-row>
                <v-col
                  cols="12"
                  md="4"
                >
                  <label class="text-subtitle-2 font-weight-bold mb-2">Sucursal de retiro</label>
                  <v-select
                    v-model="sucursal"
                    :items="sucursales"
                    outlined
                    dense
                    required
                    placeholder="Selecciona tu sucursal"
                  />
                </v-col>

                <v-col
                  cols="12"
                  md="4"
                >
                  <label class="text-subtitle-2 font-weight-bold mb-2">Fecha de retiro</label>
                  <div class="d-flex">
                    <v-text-field
                      v-model="fechaRetiro"
                      type="date"
                      outlined
                      dense
                      class="mr-2 flex-grow-1"
                    />
                    <v-select
                      v-model="horaRetiro"
                      :items="horasDisponibles"
                      outlined
                      dense
                      style="max-width: 120px"
                    />
                  </div>
                </v-col>

                <v-col
                  cols="12"
                  md="4"
                >
                  <label class="text-subtitle-2 font-weight-bold mb-2">Fecha de devolucion</label>
                  <div class="d-flex">
                    <v-text-field
                      v-model="fechaDevolucion"
                      type="date"
                      outlined
                      dense
                      class="mr-2 flex-grow-1"
                    />
                    <v-select
                      v-model="horaDevolucion"
                      :items="horasDisponibles"
                      outlined
                      dense
                      style="max-width: 120px"
                    />
                  </div>
                </v-col>
              </v-row>

              <v-row class="mt-4">
                <v-col
                  cols="12"
                  md="6"
                >
                  <v-checkbox
                    v-model="otraSucursal"
                    label="Quiero devolver en otra sucursal"
                    color="success"
                    hide-details
                  />
                </v-col>
              </v-row>
            </v-card>

            <div class="d-flex align-center">
              <v-btn
                color="primary"
                x-large
                height="64"
                min-width="150"
                class="text-h6 font-weight-bold elevation-6"
                @click="modificarReserva"
              >
                Buscar
              </v-btn>
            </div>
          </div>
        </v-card>
      </v-col>
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
      horaRetiro: '',
      horaDevolucion: '',
      otraSucursal: false,
      sucursales: [
        'Sucursal Iquique',
        'Sucursal Santiago',
        'Sucursal Antofagasta'
      ],
      horasDisponibles: [
        '08:30', '09:00', '09:30', '10:00', '10:30',
        '11:00', '11:30', '12:00', '12:30', '13:00',
        '13:30', '14:00', '14:30', '15:00', '15:30',
        '16:00', '16:30', '17:00', '17:30'
      ]
    };
  },
  methods: {
    modificarReserva() {
      alert(`Reserva modificada para la sucursal: ${this.sucursal},
             Fecha de retiro: ${this.fechaRetiro} ${this.horaRetiro},
             Fecha de devoluciÃ³n: ${this.fechaDevolucion} ${this.horaDevolucion}`);
    }
  }
};
</script>

<style scoped>
.v-text-field >>> .v-input__slot {
  min-height: 40px !important;
}
</style>

