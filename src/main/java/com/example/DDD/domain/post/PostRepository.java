package com.example.DDD.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * [Query] 게시글 목록 조회 — author만 Fetch Join (N+1 방지)
     */
    @Query("SELECT p FROM Post p JOIN FETCH p.author")
    List<Post> findAllWithAuthor();

    /**
     * [Query] 게시글 상세 조회 — author + comments + comments.author Fetch Join
     */
    @Query("SELECT DISTINCT p FROM Post p " +
            "JOIN FETCH p.author " +
            "LEFT JOIN FETCH p.comments c " +
            "LEFT JOIN FETCH c.author " +
            "WHERE p.id = :postId")
    Optional<Post> findWithDetailsById(@Param("postId") Long postId);
}
