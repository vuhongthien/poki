package com.remake.poki.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private Long userId;
    private String username;
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private MessageType type;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}