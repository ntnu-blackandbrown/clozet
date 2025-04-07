// src/services/webSocketService.js
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

class WebSocketService {
  constructor() {
    this.stompClient = null;
  }

  connect(onConnect, onError) {
    const socket = new SockJS('http://localhost:8080/ws'); // Replace with your backend URL
    this.stompClient = Stomp.over(socket);
    this.stompClient.connect({}, frame => {
      console.log('Connected: ' + frame);
      if (onConnect) onConnect(frame);
    }, error => {
      console.error('WebSocket connection error: ', error);
      if (onError) onError(error);
    });
  }

  subscribe(topic, callback) {
    if (this.stompClient) {
      return this.stompClient.subscribe(topic, response => {
        const message = JSON.parse(response.body);
        callback(message);
      });
    }
  }

  send(destination, payload) {
    if (this.stompClient) {
      this.stompClient.send(destination, {}, JSON.stringify(payload));
    }
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.disconnect(() => {
        console.log('Disconnected');
      });
    }
  }
}

export default new WebSocketService(); 