package com.taskboard.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardEvent {

    private BoardEventType type;
    private UUID boardId;
    private Object payload;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
