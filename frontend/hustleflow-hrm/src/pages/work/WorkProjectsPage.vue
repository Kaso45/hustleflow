<template>
  <div>
    <AppPageHeader
      title="Projects"
      subtitle="Manage projects and their status"
    />

    <v-card>
      <v-card-text>
        <v-row class="mb-4" dense>
          <v-col cols="12" md="3">
            <v-btn>Create Project</v-btn>
          </v-col>
          <v-col cols="12" md="3">
            <v-select label="Status" :items="['All', 'Active', 'Completed']" density="compact" />
          </v-col>
        </v-row>

        <v-table>
          <thead>
            <tr>
              <th>Name</th>
              <th>Manager</th>
              <th>Start</th>
              <th>End</th>
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
      </v-card-text>
    </v-card>
  </div>
</template>

<script setup>
import AppPageHeader from '../../components/common/AppPageHeader.vue'
</script>
