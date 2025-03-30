<template>
  <div class="verify-info">
    <h2>Registrering vellykket!</h2>
    <p>Vennligst sjekk e-posten din for å verifisere kontoen.</p>
    <p>Etter verifisering kan du logge inn.</p>
    <button @click="goToLogin">Gå til innlogging</button>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'VerifyInfo',
  setup() {
    const router = useRouter();
    const goToLogin = () => {
      router.push('/login');
    };
    return { goToLogin };
  },
});
</script>

<style scoped>
.verify-info {
  text-align: center;
  margin-top: 50px;
}
</style>
