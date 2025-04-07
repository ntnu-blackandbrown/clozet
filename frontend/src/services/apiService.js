// src/services/apiService.js
import axios from 'axios';

const API_URL = 'http://localhost:8080/api'; // Change to the correct backend URL

export default {
  getMessages() {
    return axios.get(`${API_URL}/messages`);
  },
  createMessage(messageData) {
    return axios.post(`${API_URL}/messages`, messageData);
  },
  getConversations(userId) {
    return axios.get(`${API_URL}/conversations/${userId}`);
  },
  markMessageAsRead(messageId) {
    return axios.put(`${API_URL}/messages/${messageId}/read`);
  },
  // Add more methods (update, delete, etc.) as needed
}; 