import api from './axios';
import type { CardItem } from '../types';

export const createCard = (listId: string, title: string, description?: string) =>
  api.post<CardItem>(`/lists/${listId}/cards`, { title, description });

export const updateCard = (cardId: string, data: Partial<CardItem>) =>
  api.put<CardItem>(`/cards/${cardId}`, data);

export const deleteCard = (cardId: string) =>
  api.delete(`/cards/${cardId}`);

export const moveCard = (cardId: string, targetListId: string, targetPosition: number) =>
  api.patch<CardItem>(`/cards/${cardId}/move`, { targetListId, targetPosition });
