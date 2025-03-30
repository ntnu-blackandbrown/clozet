<template>
  <div class="dashboard">
    <h2>Dashboard</h2>
    <p>Velkommen, {{ user?.username }}!</p>
    <button @click="logout">Logg ut</button>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/AuthStore'
import { useRouter } from 'vue-router'

export default defineComponent({
  name: 'Dashboard',
  setup() {
    const authStore = useAuthStore()
    const router = useRouter()
    const user = computed(() => authStore.user)

    onMounted(() => {
      if (!authStore.user) {
        router.push('/login')
      }
    })

    const logout = () => {
      authStore.logout()
      router.push('/login')
    }

    return { user, logout }
  },
})
</script>

<style scoped>
.dashboard {
  max-width: 600px;
  margin: 2rem auto;
  text-align: center;
}
</style>
