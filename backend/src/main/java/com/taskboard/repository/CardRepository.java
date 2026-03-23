package com.taskboard.repository;

import com.taskboard.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {

    List<Card> findByBoardListIdOrderByPositionAsc(UUID listId);

    @Query("SELECT COALESCE(MAX(c.position), -1) FROM Card c WHERE c.boardList.id = :listId")
    int findMaxPositionByListId(@Param("listId") UUID listId);

    @Modifying
    @Query("UPDATE Card c SET c.position = c.position - 1 WHERE c.boardList.id = :listId AND c.position > :position")
    void decrementPositionsAfter(@Param("listId") UUID listId, @Param("position") int position);

    @Modifying
    @Query("UPDATE Card c SET c.position = c.position + 1 WHERE c.boardList.id = :listId AND c.position >= :position")
    void incrementPositionsFrom(@Param("listId") UUID listId, @Param("position") int position);
}
