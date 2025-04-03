<template>
  <div class="messages-container">
    <!-- Left sidebar with messages list -->
    <MessagesSidebar :chats="chats" :active-id="activeChat" @select-chat="handleChatSelect" />

    <!-- Right chat area -->
    <ChatArea
      :active-chat="activeChat"
      :messages="chatMessages[activeChat]"
      :contact="chats.find((chat) => chat.id === activeChat)"
      @send-message="handleNewMessage"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import MessagesSidebar from '../components/messaging/MessagesSidebar.vue'
import ChatArea from '../components/messaging/ChatArea.vue'

const router = useRouter()
const route = useRoute()

const activeChat = computed(() => {
  const chatId = parseInt(route.params.chatId)
  return isNaN(chatId) ? null : chatId
})

const chats = [
  { id: 1, name: 'X-AE-A-13b', unread: 0 },
  { id: 2, name: 'Jerome White', unread: 0 },
  { id: 3, name: 'Madagascar Silver', unread: 999 },
  { id: 4, name: 'Pippins McGray', unread: 0 },
  { id: 5, name: 'McKinsey Vermillion', unread: 8 },
  { id: 6, name: 'Dorian F. Gray', unread: 2 },
  { id: 7, name: 'Benedict Combersmacks', unread: 0 },
  { id: 8, name: 'Kaori D. Miyazono', unread: 0 },
  { id: 9, name: 'Saylor Twift', unread: 0 },
  { id: 10, name: 'Miranda Blue', unread: 0 },
]

// Chat messages for each chat
const chatMessages = ref({
  1: [
    {
      id: 1,
      type: 'text',
      content: 'Initiating quantum calculations...',
      time: '09:15',
      sent: false,
    },
    {
      id: 2,
      type: 'text',
      content: 'Neural networks optimized for maximum efficiency',
      time: '09:16',
      sent: false,
    },
    {
      id: 3,
      type: 'text',
      content: 'Processing complete. Results are fascinating.',
      time: '09:20',
      sent: true,
    },
  ],
  2: [
    {
      id: 1,
      type: 'text',
      content: 'Hey, did you check the latest design updates?',
      time: '10:30',
      sent: false,
    },
    {
      id: 2,
      type: 'file',
      fileName: 'design_v2.pdf',
      fileSize: '3.2mb',
      time: '10:31',
      sent: false,
    },
    {
      id: 3,
      type: 'text',
      content: 'Yes, looks great! Just a few minor tweaks needed.',
      time: '10:45',
      sent: true,
    },
  ],
  3: [
    {
      id: 1,
      type: 'text',
      content: 'URGENT: Server maintenance required',
      time: '11:00',
      sent: false,
    },
    {
      id: 2,
      type: 'text',
      content: 'Multiple issues detected in production',
      time: '11:01',
      sent: false,
    },
    { id: 3, type: 'audio', duration: '01:30', time: '11:05', sent: true },
    {
      id: 4,
      type: 'text',
      content: 'Issues resolved. All systems operational.',
      time: '11:15',
      sent: true,
    },
  ],
  4: [
    { id: 1, type: 'text', content: 'How about a coffee break?', time: '12:00', sent: false },
    { id: 2, type: 'text', content: 'Perfect timing! Meet you in 5.', time: '12:01', sent: true },
  ],
  5: [
    { id: 1, type: 'text', content: 'Project deadline update', time: '14:20', sent: false },
    {
      id: 2,
      type: 'file',
      fileName: 'timeline_2024.xlsx',
      fileSize: '1.8mb',
      time: '14:21',
      sent: false,
    },
    { id: 3, type: 'text', content: 'Thanks, reviewing it now', time: '14:30', sent: true },
    {
      id: 4,
      type: 'text',
      content: 'Looks achievable. Team is already ahead of schedule.',
      time: '14:35',
      sent: true,
    },
  ],
  6: [
    { id: 1, type: 'text', content: 'Have you read my latest novel?', time: '15:45', sent: false },
    { id: 2, type: 'text', content: 'The one about eternal youth?', time: '15:46', sent: true },
    {
      id: 3,
      type: 'text',
      content: 'Yes! What do you think of chapter 3?',
      time: '15:47',
      sent: false,
    },
  ],
  7: [
    { id: 1, type: 'audio', duration: '02:45', time: '16:20', sent: false },
    {
      id: 2,
      type: 'text',
      content: 'Your deduction is brilliant as always!',
      time: '16:25',
      sent: true,
    },
  ],
  8: [
    {
      id: 1,
      type: 'text',
      content: 'Spring concert rehearsal tonight?',
      time: '17:00',
      sent: false,
    },
    {
      id: 2,
      type: 'file',
      fileName: 'sheet_music.pdf',
      fileSize: '4.5mb',
      time: '17:01',
      sent: false,
    },
    {
      id: 3,
      type: 'text',
      content: 'Yes, I will be there. Practicing the new piece now.',
      time: '17:05',
      sent: true,
    },
  ],
  9: [
    {
      id: 1,
      type: 'text',
      content: 'New song draft, what do you think?',
      time: '18:30',
      sent: false,
    },
    { id: 2, type: 'audio', duration: '03:15', time: '18:31', sent: false },
    {
      id: 3,
      type: 'text',
      content: 'This is amazing! Love the bridge section.',
      time: '18:40',
      sent: true,
    },
  ],
  10: [
    { id: 1, type: 'text', content: 'Azure deployment status?', time: '19:15', sent: false },
    {
      id: 2,
      type: 'text',
      content: 'All services running smoothly, no issues detected',
      time: '19:16',
      sent: true,
    },
    {
      id: 3,
      type: 'file',
      fileName: 'status_report.pdf',
      fileSize: '2.1mb',
      time: '19:17',
      sent: true,
    },
  ],
})

const handleChatSelect = (chatId) => {
  router.push(`/messages/${chatId}`)
}

const handleNewMessage = ({ chatId, message }) => {
  if (!chatMessages.value[chatId]) {
    chatMessages.value[chatId] = []
  }

  // Add an id to the message
  message.id = chatMessages.value[chatId].length + 1

  // Add the message to the chat
  chatMessages.value[chatId].push(message)

  // Simulate a response after 1 second
  setTimeout(() => {
    const responses = [
      'Thanks for your message!',
      'I will get back to you soon.',
      'Got it, working on it!',
      'Interesting point!',
      'Let me check that for you.',
    ]

    const response = {
      id: chatMessages.value[chatId].length + 1,
      type: 'text',
      content: responses[Math.floor(Math.random() * responses.length)],
      time: new Date().toLocaleTimeString('en-US', {
        hour: '2-digit',
        minute: '2-digit',
        hour12: false,
      }),
      sent: false,
    }

    chatMessages.value[chatId].push(response)
  }, 1000)
}

// Set initial chat if none selected
onMounted(() => {
  if (!activeChat.value) {
    router.replace('/messages/5')
  }
})
</script>

<style scoped>
.messages-container {
  display: flex;
  height: 100vh;
  background: #ffffff;
}
</style>
