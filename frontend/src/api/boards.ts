import api from './axios';
import type { Board, BoardRole } from '../types';

export const getBoards = () => api.get<Board[]>('/boards');

export const getBoard = (id: string) => api.get<Board>(`/boards/${id}`);

export const createBoard = (title: string, description?: string) =>
  api.post<Board>('/boards', { title, description });

export const updateBoard = (id: string, title: string, description?: string) =>
  api.put<Board>(`/boards/${id}`, { title, description });

export const deleteBoard = (id: string) => api.delete(`/boards/${id}`);

export const addMember = (boardId: string, email: string, role: BoardRole) =>
  api.post<Board>(`/boards/${boardId}/members`, { email, role });

export const removeMember = (boardId: string, userId: string) =>
  api.delete(`/boards/${boardId}/members/${userId}`);
