import { useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import type { BoardEvent } from '../types';

interface Options {
  boardId: string;
  onEvent: (event: BoardEvent) => void;
  enabled: boolean;
}

export function useWebSocket({ boardId, onEvent, enabled }: Options) {
  const clientRef = useRef<Client | null>(null);

  useEffect(() => {
    if (!enabled || !boardId) return;

    const token = localStorage.getItem('token');

    const client = new Client({
      webSocketFactory: () => new SockJS('/ws'),
      connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
      reconnectDelay: 3000,
      onConnect: () => {
        client.subscribe(`/topic/board/${boardId}`, (message) => {
          try {
            const event: BoardEvent = JSON.parse(message.body);
            onEvent(event);
          } catch {
            // ignore malformed messages
          }
        });
      },
      onStompError: (frame) => {
        console.error('STOMP error', frame);
      },
    });

    client.activate();
    clientRef.current = client;

    return () => {
      client.deactivate();
      clientRef.current = null;
    };
  }, [boardId, enabled]);   // intentionally omit onEvent to avoid reconnecting on every render

  return clientRef;
}
