<template>
  <div>
    <AppPageHeader
      title="Leave Requests"
      subtitle="Submit and review leave requests"
    />

    <v-row class="mb-4">
      <v-col cols="12" md="4">
        <v-card class="pa-4">
          <div class="text-subtitle-1 mb-2">New Leave Request</div>
          <p class="text-body-2">Skeleton form for new leave request.</p>
          <v-text-field label="Leave Type" density="compact" />
          <v-text-field label="Start Date" density="compact" />
          <v-text-field label="End Date" density="compact" />
          <v-textarea label="Reason" density="compact" />
          <v-btn class="mt-2">Submit</v-btn>
        </v-card>
      </v-col>

      <v-col cols="12" md="8">
        <v-card class="pa-4">
          <div class="text-subtitle-1 mb-2">Requests List</div>
          <v-row class="mb-2" dense>
            <v-col cols="6" md="4">
              <v-select label="Status" :items="['All', 'Pending', 'Approved', 'Rejected']" density="compact" />
            </v-col>
          </v-row>
          <v-table>
            <thead>
              <tr>
                <th>Employee</th>
                <th>Type</th>
                <th>From</th>
                <th>To</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>--</td>
                <td>--</td>
                <td>--</td>
                <td>--</td>
                <td>--</td>
                <td>
                  <v-btn size="x-small" variant="text">Approve</v-btn>
                  <v-btn size="x-small" variant="text" color="red">
                    Reject
                  </v-btn>
                </td>
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
