package com.taskboard.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BoardEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void publish(UUID boardId, BoardEventType type, Object payload) {
        BoardEvent event = BoardEvent.builder()
                .type(type)
                .boardId(boardId)
                .payload(payload)
                .build();

        messagingTemplate.convertAndSend("/topic/board/" + boardId, event);
    }
}
