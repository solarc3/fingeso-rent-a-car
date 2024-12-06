
<template>
  <v-layout>
    <v-app-bar
      title="Rent a Car"
      color="var(--primary-a0)"
    >
      <img
        src="@/assets/login-icon.svg"
        alt=""
        class="img"
      >
    </v-app-bar>
  </v-layout>
</template>

<script setup>
</script>

<style scoped>
.img{
  display: flex;
  object-fit: contain;
  height: 40px;
  padding-right: 15px;
  filter: grayscale(1);
}

</style>
