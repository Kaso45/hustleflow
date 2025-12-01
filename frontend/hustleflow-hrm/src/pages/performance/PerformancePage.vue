<template>
  <div>
    <AppPageHeader
      title="Performance"
      subtitle="View employee performance assessments"
    />

    <v-card class="pa-4">
      <v-row class="mb-4" dense>
        <v-col cols="12" md="4">
          <v-select
            label="Select Employee"
            :items="[]"
            density="compact"
          />
        </v-col>
      </v-row>

      <p class="text-body-2">
        Skeleton for performance score and comments.
      </p>
      <v-row dense class="mt-2">
        <v-col cols="12" md="3">
          <strong>Performance Score:</strong> --
        </v-col>
        <v-col cols="12" md="3">
          <strong>Review Date:</strong> --
        </v-col>
      </v-row>
      <v-row class="mt-2" dense>
        <v-col cols="12">
          <strong>Comments:</strong>
          <div>--</div>
        </v-col>
      </v-row>
    </v-card>
  </div>
</template>

<script setup>
import AppPageHeader from '../../components/common/AppPageHeader.vue'
</script>
