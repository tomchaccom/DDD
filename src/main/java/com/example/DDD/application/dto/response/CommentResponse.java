package com.example.DDD.application.dto.response;

import com.example.DDD.domain.comment.Comment;

public record CommentResponse(
        Long id,
        String content,
        Long authorId,
        String authorName
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getAuthor().getId(),
                comment.getAuthor().getUsername().getValue()
        );
    }
}
