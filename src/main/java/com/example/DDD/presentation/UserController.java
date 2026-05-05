package com.example.DDD.presentation;

import com.example.DDD.application.dto.request.UserCreateRequest;
import com.example.DDD.application.dto.request.UserUpdateRequest;
import com.example.DDD.application.dto.response.UserResponse;
import com.example.DDD.application.service.UserCommandService;
import com.example.DDD.application.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    // === Query (읽기) ===

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userQueryService.getUser(userId));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userQueryService.getAllUsers());
    }

    // === Command (쓰기) ===

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserCreateRequest request) {
        UserResponse response = userCommandService.createUser(request);
        return ResponseEntity.created(URI.create("/api/users/" + response.id()))
                .body(response);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long userId,
            @RequestBody UserUpdateRequest request
    ) {
        return ResponseEntity.ok(userCommandService.updateUser(userId, request));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userCommandService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
