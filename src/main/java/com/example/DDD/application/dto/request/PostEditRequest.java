package com.example.DDD.application.dto.request;

public record PostEditRequest(
        String title,
        String content,
        Long requesterId
) {
}
