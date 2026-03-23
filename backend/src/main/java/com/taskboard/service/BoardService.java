package com.taskboard.service;

import com.taskboard.dto.request.AddMemberRequest;
import com.taskboard.dto.request.BoardRequest;
import com.taskboard.dto.response.BoardMemberResponse;
import com.taskboard.dto.response.BoardResponse;
import com.taskboard.dto.response.ListResponse;
import com.taskboard.entity.Board;
import com.taskboard.entity.BoardMember;
import com.taskboard.entity.BoardRole;
import com.taskboard.entity.User;
import com.taskboard.repository.BoardListRepository;
import com.taskboard.repository.BoardMemberRepository;
import com.taskboard.repository.BoardRepository;
import com.taskboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final UserRepository userRepository;
    private final BoardListRepository boardListRepository;
    private final ListService listService;

    public List<BoardResponse> getMyBoards(String email) {
        User user = findUserByEmail(email);
        return boardRepository.findAllByMemberOrOwner(user.getId())
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public BoardResponse createBoard(BoardRequest request, String email) {
        User owner = findUserByEmail(email);
        Board board = Board.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .owner(owner)
                .build();
        board = boardRepository.save(board);

        BoardMember ownerMember = BoardMember.builder()
                .board(board)
                .user(owner)
                .role(BoardRole.OWNER)
                .build();
        boardMemberRepository.save(ownerMember);

        return toResponse(boardRepository.findById(board.getId()).orElseThrow());
    }

    public BoardResponse getBoard(UUID boardId, String email) {
        Board board = findBoardById(boardId);
        assertMember(boardId, email);
        return toResponse(board);
    }

    @Transactional
    public BoardResponse updateBoard(UUID boardId, BoardRequest request, String email) {
        Board board = findBoardById(boardId);
        assertOwnerOrEditor(boardId, email);
        board.setTitle(request.getTitle());
        board.setDescription(request.getDescription());
        return toResponse(boardRepository.save(board));
    }

    @Transactional
    public void deleteBoard(UUID boardId, String email) {
        findBoardById(boardId);
        assertOwner(boardId, email);
        boardRepository.deleteById(boardId);
    }

    @Transactional
    public BoardResponse addMember(UUID boardId, AddMemberRequest request, String email) {
        findBoardById(boardId);
        assertOwner(boardId, email);

        User newMember = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getEmail()));

        if (boardMemberRepository.existsByBoardIdAndUserId(boardId, newMember.getId())) {
            throw new RuntimeException("User is already a member of this board");
        }

        Board board = findBoardById(boardId);
        BoardMember member = BoardMember.builder()
                .board(board)
                .user(newMember)
                .role(request.getRole())
                .build();
        boardMemberRepository.save(member);

        return toResponse(boardRepository.findById(boardId).orElseThrow());
    }

    @Transactional
    public void removeMember(UUID boardId, UUID userId, String email) {
        findBoardById(boardId);
        assertOwner(boardId, email);

        User requester = findUserByEmail(email);
        if (requester.getId().equals(userId)) {
            throw new RuntimeException("Owner cannot remove themselves");
        }

        boardMemberRepository.deleteByBoardIdAndUserId(boardId, userId);
    }

    // --- helpers ---

    private Board findBoardById(UUID boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private void assertMember(UUID boardId, String email) {
        User user = findUserByEmail(email);
        Board board = findBoardById(boardId);
        boolean isOwner = board.getOwner().getId().equals(user.getId());
        boolean isMember = boardMemberRepository.existsByBoardIdAndUserId(boardId, user.getId());
        if (!isOwner && !isMember) {
            throw new RuntimeException("Access denied");
        }
    }

    private void assertOwnerOrEditor(UUID boardId, String email) {
        User user = findUserByEmail(email);
        BoardMember member = boardMemberRepository.findByBoardIdAndUserId(boardId, user.getId())
                .orElseThrow(() -> new RuntimeException("Access denied"));
        if (member.getRole() == BoardRole.VIEWER) {
            throw new RuntimeException("Viewers cannot modify this board");
        }
    }

    private void assertOwner(UUID boardId, String email) {
        User user = findUserByEmail(email);
        BoardMember member = boardMemberRepository.findByBoardIdAndUserId(boardId, user.getId())
                .orElseThrow(() -> new RuntimeException("Access denied"));
        if (member.getRole() != BoardRole.OWNER) {
            throw new RuntimeException("Only the owner can perform this action");
        }
    }

    private BoardResponse toResponse(Board board) {
        List<BoardMemberResponse> members = board.getMembers().stream()
                .map(m -> BoardMemberResponse.builder()
                        .userId(m.getUser().getId())
                        .username(m.getUser().getUsername())
                        .email(m.getUser().getEmail())
                        .role(m.getRole())
                        .build())
                .toList();

        List<ListResponse> lists = boardListRepository
                .findByBoardIdOrderByPositionAsc(board.getId())
                .stream().map(listService::toResponse).toList();

        return BoardResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .description(board.getDescription())
                .ownerId(board.getOwner().getId())
                .ownerUsername(board.getOwner().getUsername())
                .members(members)
                .lists(lists)
                .createdAt(board.getCreatedAt())
                .build();
    }
}
