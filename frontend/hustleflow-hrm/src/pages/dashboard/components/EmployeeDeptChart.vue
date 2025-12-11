<template>
  <div class="chart-wrap">
    <canvas ref="canvasRef"></canvas>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import { Chart, BarController, BarElement, CategoryScale, LinearScale, Tooltip, Title, Legend } from 'chart.js';
Chart.register(BarController, BarElement, CategoryScale, LinearScale, Tooltip, Title, Legend);

const props = defineProps({ data: { type: Array, default: () => [] } });

const canvasRef = ref(null);
let chart = null;

const PALETTE = [
  '#AEE3FF', // blue
  '#CFFFE8', // mint
  '#E3D9FF'  // lavender
];

function createChart(ctx, data) {
  const labels = data.map(d => d.department);
  const values = data.map(d => d.count);
  const bg = values.map((_,i) => PALETTE[i % PALETTE.length]);

  return new Chart(ctx, {
    type: 'bar',
    data: { labels, datasets: [{ label: 'Employees', data: values, backgroundColor: bg, borderRadius: 6, borderSkipped: false }] },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        x: { grid: { display: false }, ticks:{ color:'#7b8794' } },
        y: { beginAtZero: true, ticks:{ color:'#7b8794' }, grid:{ color:'rgba(7,16,40,0.04)' } }
      }
    }
  });
}

onMounted(() => {
  const ctx = canvasRef.value.getContext('2d');
  chart = createChart(ctx, props.data);
});

watch(() => props.data, (nv) => {
  if (!chart) return;
  chart.data.labels = nv.map(d => d.department);
  chart.data.datasets[0].data = nv.map(d => d.count);
  chart.data.datasets[0].backgroundColor = chart.data.datasets[0].data.map((_,i) => PALETTE[i % PALETTE.length]);
  chart.update();
}, { deep: true });
</script>

<style scoped>
.chart-wrap{ height:220px; position:relative; }
canvas {
  width: 100%;
  height: 100%;
  display: block;
  /* neutral soft lift only — no colored shadow */
  filter: drop-shadow(0 6px 12px rgba(7,16,40,0.03));
}

</style>
