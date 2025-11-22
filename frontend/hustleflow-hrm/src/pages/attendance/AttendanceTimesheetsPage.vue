<template>
  <div>
    <AppPageHeader
      title="Timesheets"
      subtitle="Check-in / Check-out and attendance history"
    />

    <v-row class="mb-4">
      <v-col cols="12" md="4">
        <v-card class="pa-4">
          <div class="text-subtitle-1 mb-2">Today Status</div>
          <p class="text-body-2">Skeleton for check-in / check-out buttons.</p>
          <v-btn class="mr-2">Clock In</v-btn>
          <v-btn>Clock Out</v-btn>
        </v-card>
      </v-col>
      <v-col cols="12" md="8">
        <v-card class="pa-4">
          <div class="text-subtitle-1 mb-2">Attendance History</div>
          <v-row class="mb-4" dense>
            <v-col cols="6" md="3">
              <v-text-field label="Month" density="compact" />
            </v-col>
            <v-col cols="6" md="3">
              <v-text-field label="Year" density="compact" />
            </v-col>
          </v-row>
          <v-table>
            <thead>
              <tr>
                <th>Date</th>
                <th>Check In</th>
                <th>Check Out</th>
                <th>Total Hours</th>
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
      </v-col>
    </v-row>
  </div>
</template>

<script setup>
import AppPageHeader from '../../components/common/AppPageHeader.vue'
</script>
