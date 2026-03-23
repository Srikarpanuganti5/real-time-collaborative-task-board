package com.taskboard.controller;

import com.taskboard.dto.request.AddMemberRequest;
import com.taskboard.dto.request.BoardRequest;
import com.taskboard.dto.response.BoardResponse;
import com.taskboard.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<List<BoardResponse>> getMyBoards(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(boardService.getMyBoards(userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<BoardResponse> createBoard(@Valid @RequestBody BoardRequest request,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(boardService.createBoard(request, userDetails.getUsername()));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponse> getBoard(@PathVariable UUID boardId,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(boardService.getBoard(boardId, userDetails.getUsername()));
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<BoardResponse> updateBoard(@PathVariable UUID boardId,
                                                     @Valid @RequestBody BoardRequest request,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(boardService.updateBoard(boardId, request, userDetails.getUsername()));
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable UUID boardId,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        boardService.deleteBoard(boardId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{boardId}/members")
    public ResponseEntity<BoardResponse> addMember(@PathVariable UUID boardId,
                                                   @Valid @RequestBody AddMemberRequest request,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(boardService.addMember(boardId, request, userDetails.getUsername()));
    }

    @DeleteMapping("/{boardId}/members/{userId}")
    public ResponseEntity<Void> removeMember(@PathVariable UUID boardId,
                                             @PathVariable UUID userId,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        boardService.removeMember(boardId, userId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
