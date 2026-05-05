package com.example.DDD.application.dto.request;

public record UserCreateRequest(
        String username,
        String email
) {
}
