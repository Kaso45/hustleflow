<template>
  <div>
    <AppPageHeader
      title="Payroll Overview"
      subtitle="View payroll records by month and year"
    />

    <v-card class="pa-4">
      <v-row class="mb-4" dense>
        <v-col cols="6" md="3">
          <v-text-field label="Month" density="compact" />
        </v-col>
        <v-col cols="6" md="3">
          <v-text-field label="Year" density="compact" />
        </v-col>
        <v-col cols="12" md="3">
          <v-select label="Status" :items="['All', 'PAID', 'UNPAID']" density="compact" />
        </v-col>
      </v-row>

      <v-table>
        <thead>
          <tr>
            <th>Employee</th>
            <th>Month</th>
            <th>Year</th>
            <th>Net Salary</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>--</td>
            <td>--</td>
            <td>--</td>
            <td>--</td>
            <td>--</td>
          </tr>
        </tbody>
      </v-table>
    </v-card>
  </div>
</template>

<script setup>
import AppPageHeader from '../../components/common/AppPageHeader.vue'
</script>
