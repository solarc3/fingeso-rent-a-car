<template>
  <v-text-field
    v-model="displayValue"
    :label="label"
    :error-messages="errorMessages"
    :required="required"
    maxlength="10"
    @input="handleInput"
    @blur="validateRut"
  />
</template>

<script setup>
import {ref, watch} from 'vue';

const props = defineProps({
  modelValue: {type: String, default: ''},
  label: {
    type: String,
    default: 'RUT'
  },
  required: {
    type: Boolean,
    default: true
  }
});

const emit = defineEmits(['update:modelValue', 'validation']);
const displayValue = ref('');
const errorMessages = ref([]);

// Validar dígito verificador
const calculateDv = (rutNumber) => {
  let sum = 0;
  let multiplier = 2;

  for (let i = String(rutNumber).length - 1; i >= 0; i--) {
    sum += Number(rutNumber[i]) * multiplier;
    multiplier = multiplier === 7 ? 2 : multiplier + 1;
  }

  const dv = 11 - (sum % 11);
  if (dv === 11) return '0';
  if (dv === 10) return 'K';
  return String(dv);
};

// Formatear RUT (XXXXXXXX-X)
const formatRut = (value) => {
  if (!value) return '';

  // Eliminar todo excepto nums y K
  let cleaned = value.replace(/[^0-9kK]/g, '').toUpperCase();

  // Limitar a 9 caracteres (8 dígitos + 1 verificador)
  if (cleaned.length > 9) {
    cleaned = cleaned.slice(0, 9);
  }

  // Si hay más de 1 caracter, separar el dígito verificador
  if (cleaned.length > 1) {
    const body = cleaned.slice(0, -1);
    const dv = cleaned.slice(-1);
    return `${body}-${dv}`;
  }

  return cleaned;
};

const validateRut = () => {
  const value = displayValue.value;
  if (!value && props.required) {
    errorMessages.value = ['RUT es requerido'];
    emit('validation', false);
    return;
  }

  if (!value) {
    errorMessages.value = [];
    emit('validation', true);
    return;
  }

  const cleaned = value.replace(/[^0-9kK]/g, '');
  if (cleaned.length < 2) {
    errorMessages.value = ['RUT inválido'];
    emit('validation', false);
    return;
  }

  const body = cleaned.slice(0, -1);
  const dv = cleaned.slice(-1).toUpperCase();
  const calculatedDv = calculateDv(body);

  if (calculatedDv !== dv) {
    errorMessages.value = ['RUT inválido'];
    emit('validation', false);
    return;
  }

  errorMessages.value = [];
  emit('validation', true);
};

const handleInput = (event) => {
  let formatted = formatRut(event.target.value);
  if (formatted.length > 10) {
    formatted = formatted.slice(0, 10);
  }
  displayValue.value = formatted;
  emit('update:modelValue', formatted);
};

watch(() => props.modelValue, (newValue) => {
  displayValue.value = formatRut(newValue);
});
</script>
