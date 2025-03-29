<template>
  <div class="messages-sidebar">
    <div class="messages-header">
      <h1>Messages <span class="message-count">29</span></h1>
      <button class="new-message-btn">
        <i class="fas fa-pen"></i>
      </button>
    </div>

    <div class="search-bar">
      <i class="fas fa-search"></i>
      <input type="text" placeholder="Search...">
    </div>

    <div class="messages-list">
      <div v-for="chat in chats" :key="chat.id" class="chat-item" :class="{ 'active': chat.id === activeId }" @click="$emit('select-chat', chat.id)">
        <div class="chat-avatar"></div>
        <div class="chat-info">
          <div class="chat-name">{{ chat.name }}</div>
          <div class="chat-preview">Enter your message description here...</div>
        </div>
        <div class="chat-meta">
          <div class="chat-time">12:25</div>
          <div v-if="chat.unread" class="unread-badge">{{ chat.unread }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  chats: {
    type: Array,
    required: true
  },
  activeId: {
    type: Number,
    default: null
  }
});

defineEmits(['select-chat']);
</script>

<style scoped>
.messages-sidebar {
  width: 350px;
  border-right: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
}

.messages-header {
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e5e7eb;
}

.messages-header h1 {
  font-size: 24px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.message-count {
  background: #e5e7eb;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 14px;
}

.new-message-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: none;
  background: transparent;
  cursor: pointer;
}

.search-bar {
  padding: 12px 20px;
  position: relative;
}

.search-bar input {
  width: 100%;
  padding: 8px 32px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
}

.search-bar i {
  position: absolute;
  left: 32px;
  top: 50%;
  transform: translateY(-50%);
  color: #6b7280;
}

.messages-list {
  flex: 1;
  overflow-y: auto;
}

.chat-item {
  display: flex;
  padding: 12px 20px;
  gap: 12px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.chat-item:hover {
  background-color: #f3f4f6;
}

.chat-item.active {
  background-color: #eff6ff;
}

.chat-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #e5e7eb;
}

.chat-info {
  flex: 1;
}

.chat-name {
  font-weight: 600;
  margin-bottom: 4px;
}

.chat-preview {
  font-size: 14px;
  color: #6b7280;
}

.chat-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.chat-time {
  font-size: 12px;
  color: #6b7280;
}

.unread-badge {
  background: #4f46e5;
  color: white;
  padding: 2px 6px;
  border-radius: 12px;
  font-size: 12px;
}
</style>
