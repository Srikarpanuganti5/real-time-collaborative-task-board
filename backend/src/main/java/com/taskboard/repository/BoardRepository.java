package com.taskboard.repository;

import com.taskboard.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardRepository extends JpaRepository<Board, UUID> {

    @Query("SELECT b FROM Board b WHERE b.owner.id = :userId OR EXISTS " +
           "(SELECT bm FROM BoardMember bm WHERE bm.board = b AND bm.user.id = :userId)")
    List<Board> findAllByMemberOrOwner(@Param("userId") UUID userId);
}
