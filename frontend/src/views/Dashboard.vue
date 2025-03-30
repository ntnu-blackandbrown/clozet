<template>
  <div class="dashboard">
    <h2>Dashboard</h2>
    <p v-if="user">Velkommen, {{ user.username }}!</p>
    <p v-else>Ingen brukerlogg funnet.</p>
    <button @click="logout">Logg ut</button>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import {useAuthStore} from "@/stores/AuthStore.ts";

export default defineComponent({
  // eslint-disable-next-line vue/multi-word-component-names
  name: 'Dashboard',
  setup() {
    const authStore = useAuthStore();
    const router = useRouter();

    // Computed for å hente innlogget bruker
    const user = computed(() => authStore.user);

    // Funksjon for å logge ut
    const logout = () => {
      authStore.logout();
      router.push('/login');
    };

    // Om brukeren ikke er logget inn, omdiriger til login-siden
    onMounted(() => {
      if (!authStore.user) {
        router.push('/login');
      }
    });

    return { user, logout };
  },
});
</script>

<style scoped>
.dashboard {
  max-width: 600px;
  margin: 2rem auto;
  text-align: center;
}
</style>
