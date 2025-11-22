<template>
  <div>
    <AppPageHeader
      title="Contracts"
      subtitle="Manage employee contracts"
    />

    <v-card>
      <v-card-text>
        <v-btn class="mb-4">
          Create Contract
        </v-btn>
        <v-table>
          <thead>
            <tr>
              <th>Employee</th>
              <th>Type</th>
              <th>Start Date</th>
              <th>End Date</th>
              <th>Status</th>
              <th>Base Salary</th>
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
