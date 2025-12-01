<template>
  <div>
    <AppPageHeader
      title="Employees"
      subtitle="Manage employee profiles"
    />

    <v-card>
      <v-card-text>
        <v-row class="mb-4" align="center">
          <v-col cols="12" md="4">
            <v-text-field
              label="Search employees"
              placeholder="Search by name, job title..."
              density="compact"
            />
          </v-col>
          <v-col cols="12" md="4">
            <v-select
              label="Department"
              :items="[]"
              density="compact"
            />
          </v-col>
        </v-row>

        <v-table>
          <thead>
            <tr>
              <th>Employee ID</th>
              <th>Department</th>
              <th>Job Title</th>
              <th>Hire Date</th>
              <th>Monthly Salary</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <!-- Skeleton: chưa lấy data, chỉ có 1 dòng mẫu -->
            <tr>
              <td>--</td>
              <td>--</td>
              <td>--</td>
              <td>--</td>
              <td>--</td>
              <td>
                <v-btn size="x-small" variant="text">View</v-btn>
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
