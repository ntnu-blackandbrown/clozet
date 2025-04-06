<script setup >
import { useWebsocket } from '@/websocket/websocket'
const websocket = useWebsocket()

const props = defineProps({
  receiverId: {
    type: String,
    required: true
  }
})

onMounted(() => {
  websocket.setReceiver(props.receiverId)
  console.log("websocket.receiver", websocket.receiver)
})

</script>

<template>

<div class="controls">
      <h3>Send Message</h3>
      <div class="form-group">
        <label>Message:</label>
        <textarea v-model="websocket.messageContent" @keypress.enter.prevent="websocket.sendMessage"></textarea>
      </div>
      <button @click="websocket.sendMessage" :disabled="!websocket.connected">Send Message</button>
    <button @click="websocket.pingServer" :disabled="!websocket.connected">Ping Server</button>
  </div>

</template>