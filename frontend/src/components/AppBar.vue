<template>
  <v-app-bar
    title=""
    color="var(--primary-a0)"
  >
    <div class="title-container">
      <a
        class="title-link"
        @click="goToHome"
      >
        Rent a Car
        <img
          src="../assets/auto.png"
          alt="Car"
          class="car-img"
        >
      </a>
    </div>
    <v-spacer />
    <v-btn
      outlined
      @click="showLoginForm"
    >
      Login
    </v-btn>
    <v-btn
      outlined
      @click="showRegisterForm"
    >
      Register
    </v-btn>
  </v-app-bar>
</template>

<script setup>
const emit = defineEmits(['showLogin', 'showRegister', 'goHome']);

const showLoginForm = () => {
  emit('showLogin');
};

const showRegisterForm = () => {
  emit('showRegister');
};

const goToHome = () => {
  emit('goHome');
};
</script>

<style scoped>
.title-container {
  display: flex;
  align-items: center;
  margin-left: 20px;
}

.title-link {
  cursor: pointer;
  color: inherit;
  text-decoration: none;
  margin-right: 10px;
  display: flex;
  align-items: center;
}

.car-img {
  height: 40px;
  object-fit: contain;
  margin-left: 10px;
}
</style>
