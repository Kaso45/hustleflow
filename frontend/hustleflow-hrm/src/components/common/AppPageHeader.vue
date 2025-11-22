<template>
  <div class="mb-4">
    <h1 class="text-h5">{{ title }}</h1>
    <p v-if="subtitle" class="text-body-2 text-medium-emphasis">
      {{ subtitle }}
    </p>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, required: true },
  subtitle: { type: String, default: '' },
})
</script>
