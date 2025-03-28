package com.example.websockettest_backend.chat.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatMessage {
    public enum MessageType {
        ENTER, TALK, LEAVE;

        @JsonCreator
        public static MessageType fromString(String value) {
            try {
                return valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                return TALK; // Default to TALK if type is unrecognized
            }
        }
    }

    @JsonProperty("type")
    private MessageType type;

    @JsonProperty("roomId")
    private String roomId;

    @JsonProperty("sender")
    private String sender;

    @JsonProperty("message")
    private String message;

    private long timestamp;
}