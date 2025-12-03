<template>
  <div>
    <AppPageHeader
      title="Dashboard"
      subtitle="Overview of HR activities"
    />

    <v-row dense>
      <v-col cols="12" md="3">
        <v-card class="pa-4">
          <div class="text-subtitle-1">Total Employees</div>
          <div class="text-h5 font-weight-bold">--</div>
        </v-card> 
      </v-col>
      <v-col cols="12" md="3">
        <v-card class="pa-4">
          <div class="text-subtitle-1">Departments</div>
          <div class="text-h5 font-weight-bold">--</div>
        </v-card>
      </v-col>
      <v-col cols="12" md="3">
        <v-card class="pa-4">
          <div class="text-subtitle-1">Active Projects</div>
          <div class="text-h5 font-weight-bold">--</div>
        </v-card>
      </v-col>
      <v-col cols="12" md="3">
        <v-card class="pa-4">
          <div class="text-subtitle-1">Pending Leave Requests</div>
          <div class="text-h5 font-weight-bold">--</div>
        </v-card>
      </v-col>
    </v-row>W

    <v-row class="mt-4" dense>
      <v-col cols="12" md="6">
        <v-card class="pa-4">
          <div class="text-subtitle-1 mb-2">Today Attendance</div>
          <p class="text-body-2">
            Skeleton placeholder for attendance summary.
          </p>
        </v-card>
      </v-col>
      <v-col cols="12" md="6">
        <v-card class="pa-4">
          <div class="text-subtitle-1 mb-2">Latest Payroll Summary</div>
          <p class="text-body-2">
            Skeleton placeholder for payroll statistics.
          </p>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script setup>
import AppPageHeader from '../../components/common/AppPageHeader.vue'
</script>
