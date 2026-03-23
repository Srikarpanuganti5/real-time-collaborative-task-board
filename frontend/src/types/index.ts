export interface User {
  userId: string;
  username: string;
  email: string;
  token: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  userId: string;
  username: string;
  email: string;
}

export type BoardRole = 'OWNER' | 'EDITOR' | 'VIEWER';

export interface BoardMember {
  userId: string;
  username: string;
  email: string;
  role: BoardRole;
}

export interface CardItem {
  id: string;
  title: string;
  description?: string;
  position: number;
  listId: string;
  assigneeId?: string;
  assigneeUsername?: string;
  dueDate?: string;
  createdAt: string;
}

export interface ListItem {
  id: string;
  title: string;
  position: number;
  boardId: string;
  cards: CardItem[];
}

export interface Board {
  id: string;
  title: string;
  description?: string;
  ownerId: string;
  ownerUsername: string;
  members: BoardMember[];
  lists: ListItem[];
  createdAt: string;
}

export type BoardEventType =
  | 'LIST_CREATED' | 'LIST_UPDATED' | 'LIST_DELETED' | 'LISTS_REORDERED'
  | 'CARD_CREATED' | 'CARD_UPDATED' | 'CARD_DELETED' | 'CARD_MOVED'
  | 'MEMBER_ADDED' | 'MEMBER_REMOVED';

export interface BoardEvent {
  type: BoardEventType;
  boardId: string;
  payload: unknown;
  timestamp: string;
}
