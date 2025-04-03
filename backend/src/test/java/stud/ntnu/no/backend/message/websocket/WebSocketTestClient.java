package stud.ntnu.no.backend.message.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WebSocketTestClient {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketTestClient.class);
    
    private final WebSocketStompClient stompClient;
    private StompSession stompSession;

    public WebSocketTestClient() {
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));

        this.stompClient = new WebSocketStompClient(new SockJsClient(transports));

        // Configure proper Jackson message converter with JavaTimeModule
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        converter.setObjectMapper(mapper);

        this.stompClient.setMessageConverter(converter);
    }

    public void connect(String url) throws Exception {
        CompletableFuture<StompSession> sessionFuture = stompClient.connectAsync(
                url, 
                new StompSessionHandlerAdapter() {
                    @Override
                    public void handleException(StompSession session, StompCommand command, 
                            StompHeaders headers, byte[] payload, Throwable exception) {
                        logger.error("Error in STOMP session: {}", exception.getMessage(), exception);
                        super.handleException(session, command, headers, payload, exception);
                    }
                    
                    @Override
                    public void handleTransportError(StompSession session, Throwable exception) {
                        logger.error("Transport error: {}", exception.getMessage(), exception);
                        super.handleTransportError(session, exception);
                    }
                    
                    @Override
                    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                        logger.info("Connected to STOMP broker");
                        super.afterConnected(session, connectedHeaders);
                    }
                }
        );
        
        try {
            this.stompSession = sessionFuture.get(10, TimeUnit.SECONDS);
            logger.info("STOMP session established");
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error("Failed to connect to WebSocket: {}", e.getMessage(), e);
            throw e;
        }
    }

    public <T> void subscribe(String destination, Class<T> type, CompletableFuture<T> future) {
        logger.info("Subscribing to {}", destination);
        stompSession.subscribe(destination, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return type;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                logger.info("Received message on {}: {}", destination, payload);
                future.complete((T) payload);
            }
        });
        logger.info("Subscribed to {}", destination);
    }

    public void send(String destination, Object message) {
        logger.info("Sending message to {}: {}", destination, message);
        stompSession.send(destination, message);
        logger.info("Message sent to {}", destination);
    }

    public void disconnect() {
        if (stompSession != null && stompSession.isConnected()) {
            logger.info("Disconnecting STOMP session");
            stompSession.disconnect();
            logger.info("STOMP session disconnected");
        }
    }
}