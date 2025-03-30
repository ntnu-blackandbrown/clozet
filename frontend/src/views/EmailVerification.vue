<template>
  <div class="email-verification">
    <h2>E-postverifisering</h2>
    <p v-if="message">{{ message }}</p>
    <button v-if="verified" @click="goToLogin">Gå til innlogging</button>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import axios from 'axios';

export default defineComponent({
  name: 'EmailVerification',
  setup() {
    const message = ref('');
    const verified = ref(false);
    const router = useRouter();
    const route = useRoute();

    onMounted(async () => {
      const token = route.query.token;
      if (token && typeof token === 'string') {
        try {
          const response = await axios.get(`/api/users/verify?token=${token}`);
          message.value = response.data;
          verified.value = true;
        } catch (error: any) {
          message.value = error.response?.data || 'Verifisering feilet';
        }
      } else {
        message.value = 'Ingen token funnet.';
      }
    });

    const goToLogin = () => {
      router.push('/login');
    };

    return { message, verified, goToLogin };
  },
});
</script>

<style scoped>
.email-verification {
  text-align: center;
  margin-top: 50px;
}
</style>
