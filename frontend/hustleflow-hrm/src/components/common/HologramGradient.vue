<template>
  <div class="holo-wrapper">
    <!-- moving blobs -->
    <div class="blob blob1"></div>
    <div class="blob blob2"></div>
    <div class="blob blob3"></div>
    <div class="blob blob4"></div>

    <!-- Branding text -->
    <div class="branding">
      <h1>HUSTLE</h1>
      <h2>MANAGEMENT</h2>
    </div>
  </div>
</template>

<style scoped>
.holo-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 24px;
  overflow: hidden;
  background: #ffffff08;
  backdrop-filter: blur(10px);
}

/* BLOBS (liquid gradient) */
.blob {
  position: absolute;
  width: 70%;
  height: 70%;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.8;
  animation: move 12s infinite ease-in-out alternate;
  mix-blend-mode: screen;
}

.blob1 {
  background: #6c5ce7;
  top: 10%;
  left: 7%;
  animation-delay: 0s;
}

.blob2 {
  background: #a29bfe;
  top: 50%;
  left: 60%;
  animation-delay: 2s;
}

.blob3 {
  background: #74b9ff;
  top: 70%;
  left: 20%;
  animation-delay: 4s;
}

.blob4 {
  background: #ff7675;
  top: 20%;
  left: 70%;
  animation-delay: 6s;
}

/* ANIMATION */
@keyframes move {
  0%   { transform: translate(0, 0) scale(1); }
  25%  { transform: translate(40px, -30px) scale(1.2); }
  50%  { transform: translate(-20px, 40px) scale(0.9); }
  75%  { transform: translate(-40px, -20px) scale(1.15); }
  100% { transform: translate(0, 0) scale(1); }
}

/* TEXT BRANDING */
.branding {
  position: absolute;
  top: 20%;
  left: 40px;
  z-index: 10;
  color: white;
  text-shadow: 0 4px 20px rgba(25, 0, 164, 0.662);
}

.branding h1 {
  font-size: 82px;
  font-weight: bolder;
  margin: 0;
  letter-spacing: 2px;
}

.branding h2 {
  font-size: 48px;
  margin: 4px 0 0 0;
  font-weight: 500;
  opacity: 0.9;
}
</style>
