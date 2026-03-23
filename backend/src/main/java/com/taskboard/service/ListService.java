package com.taskboard.service;

import com.taskboard.dto.request.ListRequest;
import com.taskboard.dto.request.ReorderListsRequest;
import com.taskboard.dto.response.CardResponse;
import com.taskboard.dto.response.ListResponse;
import com.taskboard.entity.Board;
import com.taskboard.entity.BoardList;
import com.taskboard.repository.BoardListRepository;
import com.taskboard.repository.BoardMemberRepository;
import com.taskboard.repository.BoardRepository;
import com.taskboard.repository.UserRepository;
import com.taskboard.websocket.BoardEventPublisher;
import com.taskboard.websocket.BoardEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListService {

    private final BoardListRepository boardListRepository;
    private final BoardRepository boardRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final UserRepository userRepository;
    private final BoardEventPublisher eventPublisher;

    @Transactional
    public ListResponse createList(UUID boardId, ListRequest request, String email) {
        Board board = findBoard(boardId);
        assertEditorOrOwner(boardId, email);
        int nextPosition = boardListRepository.findMaxPositionByBoardId(boardId) + 1;

        BoardList list = BoardList.builder()
                .title(request.getTitle())
                .position(nextPosition)
                .board(board)
                .build();

        ListResponse response = toResponse(boardListRepository.save(list));
        eventPublisher.publish(boardId, BoardEventType.LIST_CREATED, response);
        return response;
    }

    @Transactional
    public ListResponse updateList(UUID listId, ListRequest request, String email) {
        BoardList list = findList(listId);
        UUID boardId = list.getBoard().getId();
        assertEditorOrOwner(boardId, email);
        list.setTitle(request.getTitle());
        ListResponse response = toResponse(boardListRepository.save(list));
        eventPublisher.publish(boardId, BoardEventType.LIST_UPDATED, response);
        return response;
    }

    @Transactional
    public void deleteList(UUID listId, String email) {
        BoardList list = findList(listId);
        UUID boardId = list.getBoard().getId();
        int deletedPosition = list.getPosition();
        assertEditorOrOwner(boardId, email);
        boardListRepository.delete(list);
        boardListRepository.decrementPositionsAfter(boardId, deletedPosition);
        eventPublisher.publish(boardId, BoardEventType.LIST_DELETED, Map.of("listId", listId));
    }

    @Transactional
    public List<ListResponse> reorderLists(ReorderListsRequest request, String email) {
        assertEditorOrOwner(request.getBoardId(), email);
        List<UUID> orderedIds = request.getOrderedListIds();
        for (int i = 0; i < orderedIds.size(); i++) {
            BoardList list = findList(orderedIds.get(i));
            list.setPosition(i);
            boardListRepository.save(list);
        }
        List<ListResponse> responses = boardListRepository
                .findByBoardIdOrderByPositionAsc(request.getBoardId())
                .stream().map(this::toResponse).toList();
        eventPublisher.publish(request.getBoardId(), BoardEventType.LISTS_REORDERED, responses);
        return responses;
    }

    // --- helpers ---

    public BoardList findList(UUID listId) {
        return boardListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("List not found"));
    }

    private Board findBoard(UUID boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));
    }

    private void assertEditorOrOwner(UUID boardId, String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        var member = boardMemberRepository.findByBoardIdAndUserId(boardId, user.getId())
                .orElseThrow(() -> new RuntimeException("Access denied"));
        if (member.getRole().name().equals("VIEWER")) {
            throw new RuntimeException("Viewers cannot modify lists");
        }
    }

    public ListResponse toResponse(BoardList list) {
        List<CardResponse> cards = list.getCards().stream()
                .map(c -> CardResponse.builder()
                        .id(c.getId())
                        .title(c.getTitle())
                        .description(c.getDescription())
                        .position(c.getPosition())
                        .listId(list.getId())
                        .assigneeId(c.getAssignee() != null ? c.getAssignee().getId() : null)
                        .assigneeUsername(c.getAssignee() != null ? c.getAssignee().getUsername() : null)
                        .dueDate(c.getDueDate())
                        .createdAt(c.getCreatedAt())
                        .build())
                .toList();

        return ListResponse.builder()
                .id(list.getId())
                .title(list.getTitle())
                .position(list.getPosition())
                .boardId(list.getBoard().getId())
                .cards(cards)
                .build();
    }
}
