package com.example.DDD.application.dto.response;

import com.example.DDD.domain.post.Post;

import java.util.List;

public record PostResponse(
        Long id,
        String title,
        String content,
        Long authorId,
        String authorName,
        int commentCount,
        List<CommentResponse> comments
) {
    public static PostResponse from(Post post) {
        List<CommentResponse> commentResponses = post.getComments().stream()
                .map(CommentResponse::from)
                .toList();

        return new PostResponse(
                post.getId(),
                post.getTitle().getValue(),
                post.getContent().getValue(),
                post.getAuthor().getId(),
                post.getAuthor().getUsername().getValue(),
                post.getCommentCount(),
                commentResponses
        );
    }

    /**
     * 댓글 목록 없이 요약 형태로 반환 (목록 조회용)
     */
    public static PostResponse summaryFrom(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle().getValue(),
                post.getContent().getValue(),
                post.getAuthor().getId(),
                post.getAuthor().getUsername().getValue(),
                post.getCommentCount(),
                List.of()
        );
    }
}
