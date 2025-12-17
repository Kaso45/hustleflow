<!-- src/components/common/PastelTable.vue -->
<template>
  <table class="pastel-table">
    <thead>
      <slot name="thead" />
    </thead>
    <tbody>
      <slot />
    </tbody>
  </table>
</template>

<style scoped>
.pastel-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0 10px;
}

/* ---------- HEADER ---------- */
.pastel-table thead th {
  text-align: left;
  padding: 12px;
  font-size: 13px;
  font-weight: 500;
  color: #6b7280;
}

/* ---------- ROW ---------- */
.pastel-table tbody tr td {
  padding: 14px;
  background: #fff;
  font-size: 14px;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
}

/* Bo tròn row */
.pastel-table tbody tr td:first-child {
  border-left: 1px solid #eee;
  border-top-left-radius: 10px;
  border-bottom-left-radius: 10px;
}

.pastel-table tbody tr td:last-child {
  border-right: 1px solid #eee;
  border-top-right-radius: 10px;
  border-bottom-right-radius: 10px;
}
</style>
