package com.taskboard.controller;

import com.taskboard.dto.request.CardRequest;
import com.taskboard.dto.request.MoveCardRequest;
import com.taskboard.dto.response.CardResponse;
import com.taskboard.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping("/lists/{listId}/cards")
    public ResponseEntity<CardResponse> createCard(@PathVariable UUID listId,
                                                   @Valid @RequestBody CardRequest request,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cardService.createCard(listId, request, userDetails.getUsername()));
    }

    @GetMapping("/cards/{cardId}")
    public ResponseEntity<CardResponse> getCard(@PathVariable UUID cardId,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cardService.getCard(cardId, userDetails.getUsername()));
    }

    @PutMapping("/cards/{cardId}")
    public ResponseEntity<CardResponse> updateCard(@PathVariable UUID cardId,
                                                   @Valid @RequestBody CardRequest request,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cardService.updateCard(cardId, request, userDetails.getUsername()));
    }

    @DeleteMapping("/cards/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable UUID cardId,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        cardService.deleteCard(cardId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/cards/{cardId}/move")
    public ResponseEntity<CardResponse> moveCard(@PathVariable UUID cardId,
                                                 @Valid @RequestBody MoveCardRequest request,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cardService.moveCard(cardId, request, userDetails.getUsername()));
    }
}
