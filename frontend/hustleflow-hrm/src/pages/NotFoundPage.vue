<template>
  <div class="not-found-wrapper">
    <!-- Animated Gradient Background -->
    <div class="gradient-bg"></div>

    <!-- Main Content Card -->
    <div class="glass-content">
      <!-- Floating Icon -->
      <div class="icon-container">
        <Ghost :size="120" stroke-width="1.5" class="floating-ghost" />
        <div class="shadow-oval"></div>
      </div>

      <!-- Text Content -->
      <h1 class="title">404</h1>
      <h2 class="subtitle">Oops! You seem to be lost in space.</h2>
      <p class="description">
        The page you are looking for does not exist or has been moved to another dimension.
      </p>

      <!-- Action Button -->
      <button class="home-btn" @click="goHome">
        <ArrowLeft :size="18" />
        Return to Dashboard
      </button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { Ghost, ArrowLeft } from 'lucide-vue-next'; // Icon cực hợp với theme

const router = useRouter();

const goHome = () => {
  router.push('/dashboard');
};
</script>

<style scoped>
/* --- 1. FULL PAGE LAYOUT & ANIMATED BG --- */
.not-found-wrapper {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'Inter', sans-serif;
  color: #0b2433;
}

.gradient-bg {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
    -45deg, 
    #bfbbff, 
    #94e8ff, 
    #efffd1, 
    #abcaff, 
    #c4fff3
  );
  background-size: 400% 400%;
  animation: gradientFlow 15s ease infinite;
  z-index: 0;
}

/* --- 2. GLASS CARD --- */
.glass-content {
  position: relative;
  z-index: 10;
  background: rgba(255, 255, 255, 0.45);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  padding: 60px 40px;
  border-radius: 32px;
  text-align: center;
  max-width: 500px;
  width: 90%;
  box-shadow: 
    0 20px 50px rgba(0, 0, 0, 0.1),
    inset 0 0 0 1px rgba(255, 255, 255, 0.5);
  animation: slideUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards;
  opacity: 0;
  transform: translateY(20px);
}

/* --- 3. ANIMATED ELEMENTS --- */
.icon-container {
  position: relative;
  height: 140px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
}

.floating-ghost {
  color: #5fd1c5; /* Teal pastel */
  filter: drop-shadow(0 10px 10px rgba(95, 209, 197, 0.3));
  animation: float 3s ease-in-out infinite;
}

.shadow-oval {
  width: 60px;
  height: 8px;
  background: rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  margin-top: 10px;
  animation: shadowScale 3s ease-in-out infinite;
}

/* --- 4. TYPOGRAPHY --- */
.title {
  font-size: 80px;
  font-weight: 800;
  margin: 0;
  line-height: 1;
  background: linear-gradient(135deg, #5fd1c5 0%, #0b2433 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: -2px;
}

.subtitle {
  font-size: 20px;
  font-weight: 600;
  margin: 16px 0 8px;
  color: #0b2433;
}

.description {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 32px;
  line-height: 1.5;
}

/* --- 5. BUTTON --- */
.home-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: #0b2433;
  color: white;
  padding: 12px 28px;
  border-radius: 14px;
  font-size: 14px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 4px 12px rgba(11, 36, 51, 0.2);
}

.home-btn:hover {
  background: #1e293b;
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(11, 36, 51, 0.25);
}

.home-btn:active {
  transform: scale(0.98);
}

/* --- KEYFRAMES --- */
@keyframes gradientFlow {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

@keyframes float {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-15px); }
  100% { transform: translateY(0px); }
}

@keyframes shadowScale {
  0% { transform: scale(1); opacity: 0.2; }
  50% { transform: scale(0.7); opacity: 0.1; }
  100% { transform: scale(1); opacity: 0.2; }
}

@keyframes slideUp {
  to { opacity: 1; transform: translateY(0); }
}
</style>