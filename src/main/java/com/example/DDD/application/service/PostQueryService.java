package com.example.DDD.application.service;

import com.example.DDD.application.dto.response.PostResponse;
import com.example.DDD.domain.post.Post;
import com.example.DDD.domain.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * [Query] 게시글 읽기 전용 서비스
 * - Aggregate Root 규칙에 구속받지 않음
 * - Fetch Join으로 N+1 문제 해결
 * - readOnly 트랜잭션으로 성능 최적화
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryService {

    private final PostRepository postRepository;

    /**
     * 게시글 목록 조회 (요약)
     * - Fetch Join: Post + Author (1쿼리)
     * - 댓글은 미포함 (요약)
     */
    public List<PostResponse> getAllPosts() {
        return postRepository.findAllWithAuthor().stream()
                .map(PostResponse::summaryFrom)
                .toList();
    }

    /**
     * 게시글 상세 조회 (댓글 포함)
     * - Fetch Join: Post + Author + Comments + Comment.Author (1쿼리)
     */
    public PostResponse getPost(Long postId) {
        Post post = postRepository.findWithDetailsById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다. id=" + postId));
        return PostResponse.from(post);
    }
}
