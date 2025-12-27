<template>
  <div class="chart-wrap">
    <canvas ref="canvasRef"></canvas>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import { Chart, DoughnutController, ArcElement, Tooltip, Legend } from 'chart.js';
Chart.register(DoughnutController, ArcElement, Tooltip, Legend);

const props = defineProps({ data: { type: Object, default: () => ({ resigned:0, active:0 }) } });
const canvasRef = ref(null);
let chart = null;

function build(ctx, data) {
  return new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels: ['Resigned', 'Active'],
      datasets: [{
        data: [data.resigned || 0, data.active || 0],
        backgroundColor: [
          '#E3D9FF', // lavender mist (muted)
          '#AEE3FF'  // primary blue (muted)
        ],
        hoverBackgroundColor: ['#E3D9FF','#AEE3FF'],
        borderColor: 'rgba(255,255,255,0.04)',
        borderWidth: 1
      }]
    },
    options: {
      responsive:true,
      maintainAspectRatio:false,
      cutout: '60%',
      plugins:{ legend:{ position: 'bottom', labels:{ color:'#7b8794' } } }
    }
  });
}

onMounted(() => {
  chart = build(canvasRef.value.getContext('2d'), props.data);
});

watch(() => props.data, (nv) => {
  if (!chart) return;
  chart.data.datasets[0].data = [nv.resigned || 0, nv.active || 0];
  chart.update();
}, { deep: true });
</script>

<style scoped>
.chart-wrap{ height:200px; position:relative; }
canvas {
  width: 100%;
  height: 100%;
  display: block;
  /* neutral soft lift only — no colored shadow */
  filter: drop-shadow(0 6px 12px rgba(7,16,40,0.03));
}

</style>
