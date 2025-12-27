<template>
  <Transition name="fade">
    <div v-if="isOpen" class="modal-backdrop" @click.self="close">
      <div class="modal-card">
        <div class="modal-header">
          <div class="icon-wrap"><div class="icon"><Cog :size="24" /></div></div>
          <h3>Run Payroll</h3>
          <p>Generate salary records for the period</p>
        </div>

        <form @submit.prevent="handleSubmit" class="modal-form">
          <div class="row">
            <div class="group">
              <label>Month</label>
              <select v-model="form.month">
                <option v-for="m in 12" :key="m" :value="m">Month {{ m }}</option>
              </select>
            </div>
            <div class="group">
              <label>Year</label>
              <input v-model="form.year" type="number" />
            </div>
          </div>

          <div class="checkbox-group">
            <input type="checkbox" id="allDept" v-model="form.applyToAllDepartments">
            <label for="allDept">Apply to all departments</label>
          </div>

          <div class="dept-select" v-if="!form.applyToAllDepartments">
            <label>Select Departments</label>
            <div class="info-text">IT, Sales, HR (Coming soon in mock...)</div>
          </div>

          <div class="actions">
            <button type="button" class="cancel-btn" @click="close">Cancel</button>
            <button type="submit" class="submit-btn" :disabled="loading">
              <span v-if="loading">Processing...</span>
              <span v-else>Generate Payroll</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { Cog } from 'lucide-vue-next';

const props = defineProps({ isOpen: Boolean, loading: Boolean });
const emit = defineEmits(['close', 'submit']);

const today = new Date();
const form = reactive({
  month: today.getMonth() + 1,
  year: today.getFullYear(),
  departmentCodes: [],
  applyToAllDepartments: true
});

const close = () => emit('close');
const handleSubmit = () => emit('submit', form);
</script>

<style scoped>
.modal-backdrop { position: fixed; inset: 0; background: rgba(15,23,42,0.6); backdrop-filter: blur(4px); display: flex; justify-content: center; align-items: center; z-index: 9999; }
.modal-card { background: #fff; padding: 24px; border-radius: 16px; width: 400px; box-shadow: 0 20px 60px rgba(0,0,0,0.2); transform: scale(1); animation: popIn 0.2s cubic-bezier(0.16, 1, 0.3, 1); }
@keyframes popIn { from { transform: scale(0.95); opacity: 0; } to { transform: scale(1); opacity: 1; } }

.modal-header { text-align: center; margin-bottom: 24px; }
.icon-wrap { display: flex; justify-content: center; margin-bottom: 12px; }
.icon { width: 48px; height: 48px; background: #e0f2fe; color: #0284c7; border-radius: 12px; display: flex; align-items: center; justify-content: center; }
h3 { font-size: 18px; font-weight: 700; color: #0f172a; margin-bottom: 4px; }
p { font-size: 13px; color: #64748b; }

.row { display: flex; gap: 12px; }
.group { flex: 1; display: flex; flex-direction: column; gap: 6px; margin-bottom: 16px; }
label { font-size: 12px; font-weight: 600; color: #475569; text-transform: uppercase; }
select, input { width: 100%; padding: 8px 12px; border: 1px solid #cbd5e1; border-radius: 8px; font-size: 14px; outline: none; }
select:focus, input:focus { border-color: #0ea5e9; box-shadow: 0 0 0 2px rgba(14, 165, 233, 0.1); }

.checkbox-group { display: flex; align-items: center; gap: 8px; margin-bottom: 16px; }
.checkbox-group input { width: auto; }
.checkbox-group label { text-transform: none; color: #334155; font-size: 14px; }

.dept-select { padding: 12px; background: #f8fafc; border-radius: 8px; margin-bottom: 16px; border: 1px dashed #cbd5e1; }
.info-text { font-size: 12px; color: #94a3b8; font-style: italic; margin-top: 4px; }

.actions { display: flex; justify-content: flex-end; gap: 8px; }
.cancel-btn { padding: 8px 16px; background: transparent; border: none; font-weight: 600; color: #64748b; cursor: pointer; }
.submit-btn { padding: 8px 24px; background: #0b2433; border: none; border-radius: 8px; font-weight: 600; color: white; cursor: pointer; }
.submit-btn:disabled { opacity: 0.7; cursor: not-allowed; }
</style>