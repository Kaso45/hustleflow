<template>
  <div v-if="isOpen" class="ps-backdrop" @click.self="close">
    <div class="ps-sheet">
      <div class="ps-header">
        <div class="company-brand">
          <div class="logo">H</div>
          <div class="brand-name">HustleFlow</div>
        </div>
        <div class="slip-title">PAYSLIP</div>
      </div>

      <div class="ps-meta">
        <div class="meta-row">
          <div class="label">Period:</div>
          <div class="value">{{ data?.month }}/{{ data?.year }}</div>
        </div>
        <div class="meta-row">
          <div class="label">Employee:</div>
          <div class="value font-bold">{{ employeeName }}</div>
        </div>
        <div class="meta-row">
          <div class="label">Status:</div>
          <div :class="['value tag', data?.status]">{{ data?.status }}</div>
        </div>
      </div>

      <div class="ps-table">
        <div class="tr header">
          <div class="td">Description</div>
          <div class="td right">Amount (VND)</div>
        </div>
        <div class="tr">
          <div class="td">Base Salary</div>
          <div class="td right">{{ formatCurrency(data?.baseSalary) }}</div>
        </div>
        <div class="tr">
          <div class="td text-green-600">Bonus / Allowances</div>
          <div class="td right text-green-600">
            + {{ formatCurrency(data?.bonus) }}
          </div>
        </div>
        <div class="tr border-b">
          <div class="td text-red-500">Deductions</div>
          <div class="td right text-red-500">
            - {{ formatCurrency(data?.deduction) }}
          </div>
        </div>
        <div class="tr total">
          <div class="td">NET PAY</div>
          <div class="td right">{{ formatCurrency(data?.netSalary) }}</div>
        </div>
      </div>

      <div class="ps-footer">
        <button class="close-text" @click="close">Close</button>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({ isOpen: Boolean, data: Object, employeeName: String });
const emit = defineEmits(["close"]);
const close = () => emit("close");
const formatCurrency = (v) =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
    v || 0
  );
</script>

<style scoped>
.ps-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(3px);
  z-index: 9999;
  display: flex;
  justify-content: center;
  align-items: center;
}
.ps-sheet {
  background: #fff;
  width: 450px;
  padding: 32px;
  border-radius: 4px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  font-family: "Courier New", Courier, monospace;
}

.ps-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  border-bottom: 2px solid #000;
  padding-bottom: 12px;
}
.company-brand {
  display: flex;
  align-items: center;
  gap: 8px;
}
.logo {
  width: 24px;
  height: 24px;
  background: #000;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  border-radius: 2px;
}
.brand-name {
  font-weight: bold;
  font-family: sans-serif;
  text-transform: uppercase;
  letter-spacing: 1px;
  font-size: 14px;
}
.slip-title {
  font-size: 24px;
  font-weight: 900;
  letter-spacing: 2px;
}

.ps-meta {
  margin-bottom: 24px;
}
.meta-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 13px;
}
.label {
  color: #64748b;
  font-family: sans-serif;
}
.tag.PAID {
  color: green;
}
.tag.UNPAID {
  color: red;
}

.ps-table {
  border: 1px solid #e2e8f0;
  margin-bottom: 24px;
  font-family: sans-serif;
}
.tr {
  display: flex;
  justify-content: space-between;
  padding: 10px 12px;
}
.tr.header {
  background: #f8fafc;
  font-weight: 600;
  font-size: 12px;
  text-transform: uppercase;
  color: #64748b;
  border-bottom: 1px solid #e2e8f0;
}
.tr.total {
  background: #000;
  color: #fff;
  font-weight: bold;
  font-size: 16px;
  margin-top: -1px;
}
.border-b {
  border-bottom: 1px solid #e2e8f0;
}
.td.right {
  text-align: right;
}

.ps-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.close-text {
  border: none;
  background: transparent;
  text-decoration: underline;
  cursor: pointer;
  font-size: 12px;
}
</style>
