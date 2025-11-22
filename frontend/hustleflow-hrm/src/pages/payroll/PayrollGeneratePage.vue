<template>
  <div>
    <AppPageHeader
      title="Generate Payroll"
      subtitle="Generate payroll for company or specific departments"
    />

    <v-card class="pa-4">
      <v-row dense>
        <v-col cols="6" md="3">
          <v-text-field label="Month" density="compact" />
        </v-col>
        <v-col cols="6" md="3">
          <v-text-field label="Year" density="compact" />
        </v-col>
        <v-col cols="12" md="3">
          <v-switch label="Apply to all departments" />
        </v-col>
      </v-row>

      <v-row class="mt-2" dense>
        <v-col cols="12" md="6">
          <v-select
            label="Departments"
            :items="[]"
            multiple
            density="compact"
          />
        </v-col>
      </v-row>

      <v-btn class="mt-4">
        Generate
      </v-btn>
    </v-card>
  </div>
</template>

<script setup>
import AppPageHeader from '../../components/common/AppPageHeader.vue'
</script>
