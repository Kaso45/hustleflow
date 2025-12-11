<template>
  <div class="chart-wrap">
    <canvas ref="canvasRef"></canvas>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { Chart, BarController, BarElement, CategoryScale, LinearScale, Tooltip } from 'chart.js';
Chart.register(BarController, BarElement, CategoryScale, LinearScale, Tooltip);

const props = defineProps({ data: { type: Object, default: () => ({}) } });
const canvasRef = ref(null);
let chart = null;

function build(ctx, data) {
  const labels = Object.keys(data).sort();
  const values = labels.map(l => data[l]);
  return new Chart(ctx, {
    type: 'bar',
    data: { labels, datasets: [{ label:'Count', data: values, backgroundColor: '#E3D9FF', borderRadius: 6, borderSkipped:false }] },
    options: { responsive:true, maintainAspectRatio:false, plugins:{ legend:{ display:false } }, scales:{ y:{ beginAtZero:true, grid:{ color:'rgba(7,16,40,0.04)' }, ticks:{ color:'#7b8794' } }, x:{ ticks:{ color:'#7b8794' } } } }
  });
}

onMounted(() => { chart = build(canvasRef.value.getContext('2d'), props.data); });

watch(() => props.data, (nv) => {
  if (!chart) return;
  const labels = Object.keys(nv).sort();
  chart.data.labels = labels;
  chart.data.datasets[0].data = labels.map(l => nv[l] || 0);
  chart.update();
}, { deep: true });
</script>

<style scoped>
.chart-wrap{ height:180px; position:relative; }
canvas {
  width: 100%;
  height: 100%;
  display: block;
  /* neutral soft lift only — no colored shadow */
  filter: drop-shadow(0 6px 12px rgba(255, 255, 255, 0.03));
}

</style>
