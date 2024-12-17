<template>
  <v-container
    fluid
    class="pa-0"
  >
    <v-row no-gutters>
      <v-img
        src="../assets/about.png"
        max-width="800"
        class="mx-auto mb-4"
      />

      <v-col
        cols="12"
        md="6"
        class="d-flex align-center justify-center pa-4"
        style="background-color: #ffffff;"
      >
        <v-col
          v-for="(value, index) in values"
          :key="index"
          class="d-flex flex-column align-items-center my-6"
          style="margin-bottom: 30px;"
        >
          <img
            :src="`/public/${value.icon}`"
            alt="icon"
            width="48"
            height="48"
            class="mb-3"
          >
          <h3
            class="font-weight-bold mb-2"
            style="text-align: center;"
          >
            {{ value.title }}
          </h3>
          <p
            class="text-body-1"
            style="color: #555; text-align: justify; margin: 0; width: 90%;"
          >
            {{ value.description }}
          </p>
        </v-col>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  data() {
    return {
      values: [
        {
          icon: 'building.png',
          title: '¿Quiénes somos?',
          description: 'Somos una empresa dedicada al arriendo vehicular enfocada en mejorar la movilidad en Chile, creando comunidades conectadas y facilitando el acceso a vehículos de calidad para todos.'
        },
        {
          icon: 'people.png',
          title: 'Atención al cliente',
          description: 'Valoramos a nuestros clientes, por lo que ofrecemos una variedad de opciones de vehículos para satisfacer las distintas necesidades de transporte de cada persona o familia.'
        },
        {
          icon: 'statistics.png',
          title: 'Impacto',
          description: 'Nos enfocamos en resultados que mejoren la experiencia de nuestros usuarios y hagan la movilidad más accesible para todos desde donde estes.'
        },
        {
          icon: 'check.png',  // Nombre de la imagen
          title: '¡Hazte parte!',
          description: 'Queremos que todos puedan formar parte de nuestro viaje, ofreciendo un servicio transparente, accesible y dispuesto a aprender de cada experiencia para mejorar continuamente.'
        }
      ]
    }
  }
}
</script>

<style scoped>
.v-col {
  height: 100vh;
}

.d-flex {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
}

.my-6 {
  margin-bottom: 24px;
}

h3 {
  text-align: center;
}

p {
  text-align: justify;
  color: #555;
  width: 90%;
}
</style>


