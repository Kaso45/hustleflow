<template>
  <div>
    <AppPageHeader
      title="Tasks"
      subtitle="Manage tasks across projects"
    />

    <v-card>
      <v-card-text>
        <v-row class="mb-4" dense>
          <v-col cols="12" md="3">
            <v-btn>Create Task</v-btn>
          </v-col>
          <v-col cols="12" md="3">
            <v-select label="Status" :items="['All', 'TODO', 'IN_PROGRESS', 'DONE']" density="compact" />
          </v-col>
        </v-row>

        <v-table>
          <thead>
            <tr>
              <th>Project</th>
              <th>Assignee</th>
              <th>Title</th>
              <th>Deadline</th>
              <th>Priority</th>
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
