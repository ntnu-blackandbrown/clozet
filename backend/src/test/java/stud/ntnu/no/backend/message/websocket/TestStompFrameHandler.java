package stud.ntnu.no.backend.message.websocket;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;

public class TestStompFrameHandler implements StompFrameHandler {
    private final BlockingQueue<String> queue;

    public TestStompFrameHandler(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return String.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        queue.add((String) payload);
    }
} 