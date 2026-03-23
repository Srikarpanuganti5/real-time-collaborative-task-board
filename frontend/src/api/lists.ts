import api from './axios';
import type { ListItem } from '../types';

export const createList = (boardId: string, title: string) =>
  api.post<ListItem>(`/boards/${boardId}/lists`, { title });

export const updateList = (listId: string, title: string) =>
  api.put<ListItem>(`/lists/${listId}`, { title });

export const deleteList = (listId: string) =>
  api.delete(`/lists/${listId}`);

export const reorderLists = (boardId: string, orderedListIds: string[]) =>
  api.patch<ListItem[]>('/lists/reorder', { boardId, orderedListIds });
