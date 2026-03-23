package com.taskboard.repository;

import com.taskboard.entity.BoardMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BoardMemberRepository extends JpaRepository<BoardMember, UUID> {
    Optional<BoardMember> findByBoardIdAndUserId(UUID boardId, UUID userId);
    boolean existsByBoardIdAndUserId(UUID boardId, UUID userId);
    void deleteByBoardIdAndUserId(UUID boardId, UUID userId);
}
