package com.example.DDD.application.dto.response;

import com.example.DDD.domain.user.User;

public record UserResponse(
        Long id,
        String username,
        String email
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername().getValue(),
                user.getEmail().getValue()
        );
    }
}
