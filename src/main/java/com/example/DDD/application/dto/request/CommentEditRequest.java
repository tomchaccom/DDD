package com.example.DDD.application.dto.request;

public record CommentEditRequest(
        String content,
        Long requesterId
) {
}
