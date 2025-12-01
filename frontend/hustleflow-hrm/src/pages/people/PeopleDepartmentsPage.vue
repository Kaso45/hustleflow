<template>
  <div>
    <AppPageHeader
      title="Departments"
      subtitle="Manage company departments"
    />

    <v-card>
      <v-card-text>
        <v-row class="mb-4" align="center">
          <v-col cols="12" md="4">
            <v-btn>Create Department</v-btn>
          </v-col>
        </v-row>
        <v-table>
          <thead>
            <tr>
              <th>Name</th>
              <th>Code</th>
              <th>Manager</th>
              <th>Description</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>--</td>
              <td>--</td>
              <td>--</td>
              <td>--</td>
              <td>
                <v-btn size="x-small" variant="text">Edit</v-btn>
                <v-btn size="x-small" variant="text" color="red">
                  Delete
                </v-btn>
              </td>
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
