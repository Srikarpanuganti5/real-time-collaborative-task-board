package com.taskboard.controller;

import com.taskboard.dto.request.ListRequest;
import com.taskboard.dto.request.ReorderListsRequest;
import com.taskboard.dto.response.ListResponse;
import com.taskboard.service.ListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ListController {

    private final ListService listService;

    @PostMapping("/api/boards/{boardId}/lists")
    public ResponseEntity<ListResponse> createList(@PathVariable UUID boardId,
                                                   @Valid @RequestBody ListRequest request,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(listService.createList(boardId, request, userDetails.getUsername()));
    }

    @PutMapping("/api/lists/{listId}")
    public ResponseEntity<ListResponse> updateList(@PathVariable UUID listId,
                                                   @Valid @RequestBody ListRequest request,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(listService.updateList(listId, request, userDetails.getUsername()));
    }

    @DeleteMapping("/api/lists/{listId}")
    public ResponseEntity<Void> deleteList(@PathVariable UUID listId,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        listService.deleteList(listId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/api/lists/reorder")
    public ResponseEntity<List<ListResponse>> reorderLists(@Valid @RequestBody ReorderListsRequest request,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(listService.reorderLists(request, userDetails.getUsername()));
    }
}
