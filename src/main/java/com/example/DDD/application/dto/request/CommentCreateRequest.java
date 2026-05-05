package com.example.DDD.application.dto.request;

public record CommentCreateRequest(
        String content,
        Long commenterId
) {
}
