package com.example.DDD.application.dto.request;

public record PostCreateRequest(
        String title,
        String content,
        Long authorId
) {
}
