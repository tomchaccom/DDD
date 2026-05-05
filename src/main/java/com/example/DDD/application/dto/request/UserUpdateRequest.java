package com.example.DDD.application.dto.request;

public record UserUpdateRequest(
        String username,
        String email
) {
}
