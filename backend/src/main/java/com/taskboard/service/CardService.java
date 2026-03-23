package com.taskboard.service;

import com.taskboard.dto.request.CardRequest;
import com.taskboard.dto.request.MoveCardRequest;
import com.taskboard.dto.response.CardResponse;
import com.taskboard.entity.BoardList;
import com.taskboard.entity.Card;
import com.taskboard.entity.User;
import com.taskboard.repository.BoardMemberRepository;
import com.taskboard.repository.CardRepository;
import com.taskboard.repository.UserRepository;
import com.taskboard.websocket.BoardEventPublisher;
import com.taskboard.websocket.BoardEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final UserRepository userRepository;
    private final ListService listService;
    private final BoardEventPublisher eventPublisher;

    @Transactional
    public CardResponse createCard(UUID listId, CardRequest request, String email) {
        BoardList list = listService.findList(listId);
        UUID boardId = list.getBoard().getId();
        assertEditorOrOwner(boardId, email);

        int nextPosition = cardRepository.findMaxPositionByListId(listId) + 1;

        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId()).orElse(null);
        }

        Card card = Card.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .position(nextPosition)
                .boardList(list)
                .assignee(assignee)
                .dueDate(request.getDueDate())
                .build();

        CardResponse response = toResponse(cardRepository.save(card));
        eventPublisher.publish(boardId, BoardEventType.CARD_CREATED, response);
        return response;
    }

    public CardResponse getCard(UUID cardId, String email) {
        Card card = findCard(cardId);
        assertMember(card.getBoardList().getBoard().getId(), email);
        return toResponse(card);
    }

    @Transactional
    public CardResponse updateCard(UUID cardId, CardRequest request, String email) {
        Card card = findCard(cardId);
        UUID boardId = card.getBoardList().getBoard().getId();
        assertEditorOrOwner(boardId, email);

        card.setTitle(request.getTitle());
        card.setDescription(request.getDescription());
        card.setDueDate(request.getDueDate());

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId()).orElse(null);
            card.setAssignee(assignee);
        } else {
            card.setAssignee(null);
        }

        CardResponse response = toResponse(cardRepository.save(card));
        eventPublisher.publish(boardId, BoardEventType.CARD_UPDATED, response);
        return response;
    }

    @Transactional
    public void deleteCard(UUID cardId, String email) {
        Card card = findCard(cardId);
        UUID listId = card.getBoardList().getId();
        UUID boardId = card.getBoardList().getBoard().getId();
        int deletedPosition = card.getPosition();
        assertEditorOrOwner(boardId, email);
        cardRepository.delete(card);
        cardRepository.decrementPositionsAfter(listId, deletedPosition);
        eventPublisher.publish(boardId, BoardEventType.CARD_DELETED, Map.of("cardId", cardId, "listId", listId));
    }

    @Transactional
    public CardResponse moveCard(UUID cardId, MoveCardRequest request, String email) {
        Card card = findCard(cardId);
        UUID boardId = card.getBoardList().getBoard().getId();
        assertEditorOrOwner(boardId, email);

        UUID sourceListId = card.getBoardList().getId();
        int sourcePosition = card.getPosition();
        UUID targetListId = request.getTargetListId();
        int targetPosition = request.getTargetPosition();

        BoardList targetList = listService.findList(targetListId);

        cardRepository.decrementPositionsAfter(sourceListId, sourcePosition);
        cardRepository.incrementPositionsFrom(targetListId, targetPosition);

        card.setBoardList(targetList);
        card.setPosition(targetPosition);

        CardResponse response = toResponse(cardRepository.save(card));
        eventPublisher.publish(boardId, BoardEventType.CARD_MOVED, response);
        return response;
    }

    // --- helpers ---

    private Card findCard(UUID cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
    }

    private void assertMember(UUID boardId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        boolean isMember = boardMemberRepository.existsByBoardIdAndUserId(boardId, user.getId());
        if (!isMember) throw new RuntimeException("Access denied");
    }

    private void assertEditorOrOwner(UUID boardId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        var member = boardMemberRepository.findByBoardIdAndUserId(boardId, user.getId())
                .orElseThrow(() -> new RuntimeException("Access denied"));
        if (member.getRole().name().equals("VIEWER")) {
            throw new RuntimeException("Viewers cannot modify cards");
        }
    }

    public CardResponse toResponse(Card card) {
        return CardResponse.builder()
                .id(card.getId())
                .title(card.getTitle())
                .description(card.getDescription())
                .position(card.getPosition())
                .listId(card.getBoardList().getId())
                .assigneeId(card.getAssignee() != null ? card.getAssignee().getId() : null)
                .assigneeUsername(card.getAssignee() != null ? card.getAssignee().getUsername() : null)
                .dueDate(card.getDueDate())
                .createdAt(card.getCreatedAt())
                .build();
    }
}
