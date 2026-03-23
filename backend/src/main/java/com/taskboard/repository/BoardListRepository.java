package com.taskboard.repository;

import com.taskboard.entity.BoardList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardListRepository extends JpaRepository<BoardList, UUID> {

    List<BoardList> findByBoardIdOrderByPositionAsc(UUID boardId);

    @Query("SELECT COALESCE(MAX(l.position), -1) FROM BoardList l WHERE l.board.id = :boardId")
    int findMaxPositionByBoardId(@Param("boardId") UUID boardId);

    @Modifying
    @Query("UPDATE BoardList l SET l.position = l.position - 1 WHERE l.board.id = :boardId AND l.position > :position")
    void decrementPositionsAfter(@Param("boardId") UUID boardId, @Param("position") int position);
}
