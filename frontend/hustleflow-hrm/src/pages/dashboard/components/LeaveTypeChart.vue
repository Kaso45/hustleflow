<template>
  <div class="chart-wrap">
    <canvas ref="canvasRef"></canvas>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { Chart, PieController, ArcElement, Tooltip, Legend } from 'chart.js';
Chart.register(PieController, ArcElement, Tooltip, Legend);

const props = defineProps({ data: { type: Object, default: () => ({}) } });
const canvasRef = ref(null);
let chart = null;

function build(ctx, data) {
  const labels = Object.keys(data);
  const values = labels.map(l => data[l]);
  const palette = ['#AEE3FF','#CFFFE8','#E3D9FF'];
  return new Chart(ctx, {
    type: 'pie',
    data:{
      labels,
      datasets:[{
        data: values,
        backgroundColor: labels.map((_,i)=>palette[i % palette.length]),
        borderColor: 'rgba(255,255,255,0.04)',
        borderWidth: 1
      }]
    },
    options:{
      responsive:true,
      maintainAspectRatio:false,
      plugins:{ legend:{ position:'bottom', labels:{ color:'#7b8794' } } }
    }
  });
}

onMounted(() => { chart = build(canvasRef.value.getContext('2d'), props.data); });

watch(() => props.data, (nv) => {
  if (!chart) return;
  const labels = Object.keys(nv);
  chart.data.labels = labels;
  chart.data.datasets[0].data = labels.map(l => nv[l] || 0);
  chart.data.datasets[0].backgroundColor = labels.map((_,i)=> ['#AEE3FF','#CFFFE8','#E3D9FF'][i % 3]);
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
  filter: drop-shadow(0 6px 12px rgba(7,16,40,0.03));
}

</style>
