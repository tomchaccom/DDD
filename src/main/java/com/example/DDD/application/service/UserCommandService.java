package com.example.DDD.application.service;

import com.example.DDD.application.dto.request.UserCreateRequest;
import com.example.DDD.application.dto.request.UserUpdateRequest;
import com.example.DDD.application.dto.response.UserResponse;
import com.example.DDD.domain.user.User;
import com.example.DDD.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * [Command] 유저 쓰기 전용 서비스
 * - 생성/수정/삭제 + 다른 Command 서비스에서 사용하는 엔티티 조회
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandService {

    private final UserRepository userRepository;

    /**
     * 회원 가입
     */
    public UserResponse createUser(UserCreateRequest request) {
        User user = User.create(request.username(), request.email());
        User savedUser = userRepository.save(user);
        return UserResponse.from(savedUser);
    }

    /**
     * 회원 정보 수정 — 비즈니스 로직은 엔티티/값 객체에 위임
     */
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = findUserById(userId);

        if (request.username() != null) {
            user.changeUsername(request.username());
        }
        if (request.email() != null) {
            user.changeEmail(request.email());
        }

        return UserResponse.from(user);
    }

    /**
     * 회원 삭제
     */
    public void deleteUser(Long userId) {
        User user = findUserById(userId);
        userRepository.delete(user);
    }

    /**
     * 엔티티 조회 헬퍼 — 다른 Command 서비스에서도 사용
     */
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id=" + userId));
    }
}
