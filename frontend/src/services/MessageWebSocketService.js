import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import { ref } from 'vue';

// Create reactive state that can be imported and used across components
const connectionState = ref('disconnected'); // 'disconnected', 'connecting', 'connected'
const stompClient = ref(null);

// Store for our subscriptions
const subscriptions = ref({});

const API_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const MessageWebSocketService = {
  connectionState,
  
  /**
   * Connect to the WebSocket server
   * @returns Promise that resolves when connected or rejects on error
   */
  connect() {
    return new Promise((resolve, reject) => {
      if (connectionState.value === 'connected') {
        resolve();
        return;
      }
      
      connectionState.value = 'connecting';
      
      try {
        // Use SockJS for WebSocket compatibility
        const socket = new SockJS(`${API_URL}/ws`);
        
        // Create STOMP client over SockJS
        const client = Stomp.over(socket);
        
        // Reduce logging noise in production
        if (import.meta.env.PROD) {
          client.debug = null;
        }
        
        // Connect with STOMP
        client.connect(
          {},
          frame => {
            console.log('Connected to WebSocket:', frame);
            stompClient.value = client;
            connectionState.value = 'connected';
            resolve(frame);
          },
          error => {
            console.error('WebSocket connection error:', error);
            connectionState.value = 'disconnected';
            stompClient.value = null;
            reject(error);
          }
        );
        
        // Set heartbeat to keep connection alive
        client.heartbeat.outgoing = 20000; // Send heartbeat every 20 seconds
        client.heartbeat.incoming = 20000; // Expect heartbeat every 20 seconds
      } catch (error) {
        console.error('Error creating WebSocket connection:', error);
        connectionState.value = 'disconnected';
        reject(error);
      }
    });
  },
  
  /**
   * Subscribe to a STOMP destination
   * @param {string} destination - The STOMP destination/topic to subscribe to
   * @param {Function} callback - Callback function when a message is received
   * @param {string} subscriptionId - Optional ID to track this subscription
   * @returns The subscription object
   */
  subscribe(destination, callback, subscriptionId = null) {
    if (!stompClient.value || connectionState.value !== 'connected') {
      console.error('Cannot subscribe: Not connected to WebSocket');
      return null;
    }
    
    try {
      const subscription = stompClient.value.subscribe(destination, message => {
        try {
          const payload = JSON.parse(message.body);
          callback(payload, message);
        } catch (error) {
          console.error('Error parsing message:', error, message.body);
          callback(message.body, message);
        }
      });
      
      // Store subscription if ID is provided
      if (subscriptionId) {
        subscriptions.value[subscriptionId] = subscription;
      }
      
      return subscription;
    } catch (error) {
      console.error(`Error subscribing to ${destination}:`, error);
      return null;
    }
  },
  
  /**
   * Send a message to a STOMP destination
   * @param {string} destination - The STOMP destination/endpoint
   * @param {any} payload - The message payload to send
   * @param {Object} headers - Optional STOMP headers
   * @returns {boolean} Success status
   */
  send(destination, payload, headers = {}) {
    if (!stompClient.value || connectionState.value !== 'connected') {
      console.error('Cannot send: Not connected to WebSocket');
      return false;
    }
    
    try {
      const stringPayload = typeof payload === 'string' 
        ? payload 
        : JSON.stringify(payload);
        
      stompClient.value.send(destination, headers, stringPayload);
      return true;
    } catch (error) {
      console.error(`Error sending to ${destination}:`, error);
      return false;
    }
  },
  
  /**
   * Unsubscribe from a specific subscription
   * @param {string} subscriptionId - The ID of the subscription to unsubscribe
   */
  unsubscribe(subscriptionId) {
    if (subscriptions.value[subscriptionId]) {
      try {
        subscriptions.value[subscriptionId].unsubscribe();
        delete subscriptions.value[subscriptionId];
      } catch (error) {
        console.error(`Error unsubscribing from ${subscriptionId}:`, error);
      }
    }
  },
  
  /**
   * Disconnect from the WebSocket server
   */
  disconnect() {
    if (stompClient.value && connectionState.value === 'connected') {
      try {
        // Unsubscribe from all subscriptions
        Object.values(subscriptions.value).forEach(subscription => {
          subscription.unsubscribe();
        });
        
        // Clear subscriptions
        subscriptions.value = {};
        
        // Disconnect STOMP client
        stompClient.value.disconnect(() => {
          console.log('Disconnected from WebSocket');
          connectionState.value = 'disconnected';
          stompClient.value = null;
        });
      } catch (error) {
        console.error('Error disconnecting from WebSocket:', error);
        connectionState.value = 'disconnected';
        stompClient.value = null;
      }
    }
  }
};

export default MessageWebSocketService; 