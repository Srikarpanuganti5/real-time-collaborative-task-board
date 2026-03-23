# Real-Time Collaborative Task Board

A full-stack real-time collaborative task board application (like Trello) built with:

## Tech Stack

### Backend
- Java 21 + Spring Boot 3.5
- Spring Security + JWT Authentication
- WebSocket (STOMP over SockJS)
- PostgreSQL + Spring Data JPA
- Lombok

### Frontend
- React + TypeScript
- Tailwind CSS
- react-beautiful-dnd (drag & drop)
- SockJS + STOMP (real-time)

## Project Structure

```
real-time-collaborative-task-board/
├── backend/      # Spring Boot API
└── frontend/     # React TypeScript app
```

## Modules

| # | Module | Status |
|---|--------|--------|
| 1 | Backend Project Setup | ✅ |
| 2 | Backend Auth (JWT) | 🔄 |
| 3 | Backend Boards & Members | ⏳ |
| 4 | Backend Lists & Cards | ⏳ |
| 5 | Backend WebSockets | ⏳ |
| 6 | Frontend Setup | ⏳ |
| 7 | Frontend Auth UI | ⏳ |
| 8 | Frontend Board View | ⏳ |
| 9 | Drag & Drop | ⏳ |
| 10 | Frontend WebSocket | ⏳ |

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.9+
- PostgreSQL 16+
- Node.js 18+

### Backend
```bash
cd backend
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```
