package com.taskboard.websocket;

public enum BoardEventType {
    // List events
    LIST_CREATED,
    LIST_UPDATED,
    LIST_DELETED,
    LISTS_REORDERED,

    // Card events
    CARD_CREATED,
    CARD_UPDATED,
    CARD_DELETED,
    CARD_MOVED,

    // Member events
    MEMBER_ADDED,
    MEMBER_REMOVED
}
