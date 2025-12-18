package com.remake.poki.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.remake.poki.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private static final Map<String, String> userNames = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper; // ✅ INJECT ObjectMapper

    // ✅ CONSTRUCTOR INJECTION
    public ChatWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        sessions.put(sessionId, session);
        log.info("✅ New connection: {}", sessionId);
        log.info("📊 Total connections: {}", sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("📨 Message received: {}", payload);

        try {
            // ✅ PARSE JSON
            ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
            chatMessage.setTimestamp(LocalDateTime.now());

            String sessionId = session.getId();

            if ("JOIN".equals(chatMessage.getType().name())) {
                userNames.put(sessionId, chatMessage.getUsername());
                log.info("👤 User joined: {}", chatMessage.getUsername());
            }

            // ✅ BROADCAST
            broadcastMessage(chatMessage);

        } catch (Exception e) {
            log.error("❌ Error processing message: {}", e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        String username = userNames.get(sessionId);

        sessions.remove(sessionId);
        userNames.remove(sessionId);

        log.info("❌ Connection closed: {} ({})", sessionId, status);
        log.info("📊 Total connections: {}", sessions.size());

        if (username != null) {
            ChatMessage leaveMessage = new ChatMessage();
            leaveMessage.setUsername(username);
            leaveMessage.setType(ChatMessage.MessageType.LEAVE);
            leaveMessage.setTimestamp(LocalDateTime.now());

            broadcastMessage(leaveMessage);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("⚠️ Transport error: {}", exception.getMessage());
        session.close(CloseStatus.SERVER_ERROR);
    }

    private void broadcastMessage(ChatMessage chatMessage) {
        String json;
        try {
            // ✅ SERIALIZE with ObjectMapper
            json = objectMapper.writeValueAsString(chatMessage);
        } catch (Exception e) {
            log.error("❌ Error converting to JSON: {}", e.getMessage());
            return;
        }

        log.info("📤 Broadcasting to {} clients: {}", sessions.size(), json);

        sessions.values().forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(json));
                }
            } catch (IOException e) {
                log.error("❌ Error sending message to {}: {}", session.getId(), e.getMessage());
            }
        });
    }
}