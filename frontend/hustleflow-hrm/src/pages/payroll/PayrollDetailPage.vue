<template>
  <div>
    <AppPageHeader
      title="Payroll Detail"
      subtitle="View payroll details of a single employee"
    />

    <v-card class="pa-4">
      <p class="text-body-2 mb-4">
        Skeleton view: later you can add routing params or filters to select the employee and month/year.
      </p>

      <v-row dense>
        <v-col cols="12" md="4">
          <strong>Employee:</strong> --
        </v-col>
        <v-col cols="6" md="4">
          <strong>Month:</strong> --
        </v-col>
        <v-col cols="6" md="4">
          <strong>Year:</strong> --
        </v-col>
      </v-row>

      <v-divider class="my-4" />

      <v-row dense>
        <v-col cols="12" md="3">
          <strong>Base Salary:</strong> --
        </v-col>
        <v-col cols="12" md="3">
          <strong>Bonus:</strong> --
        </v-col>
        <v-col cols="12" md="3">
          <strong>Deduction:</strong> --
        </v-col>
        <v-col cols="12" md="3">
          <strong>Net Salary:</strong> --
        </v-col>
      </v-row>

      <v-row class="mt-4" dense>
        <v-col cols="12" md="3">
          <strong>Status:</strong> --
        </v-col>
      </v-row>
    </v-card>
  </div>
</template>

<script setup>
import AppPageHeader from '../../components/common/AppPageHeader.vue'
</script>
