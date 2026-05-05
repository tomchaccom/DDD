package com.example.DDD.application.service;

import com.example.DDD.application.dto.request.UserCreateRequest;
import com.example.DDD.application.dto.request.UserUpdateRequest;
import com.example.DDD.application.dto.response.UserResponse;
import com.example.DDD.domain.user.User;
import com.example.DDD.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    /**
     * 회원 가입
     */
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        User user = User.create(request.username(), request.email());
        User savedUser = userRepository.save(user);
        return UserResponse.from(savedUser);
    }

    /**
     * 회원 단건 조회
     */
    public UserResponse getUser(Long userId) {
        User user = findUserById(userId);
        return UserResponse.from(user);
    }

    /**
     * 회원 목록 조회
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    /**
     * 회원 정보 수정
     * - 비즈니스 로직(검증)은 엔티티/값 객체에 위임
     */
    @Transactional
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
    @Transactional
    public void deleteUser(Long userId) {
        User user = findUserById(userId);
        userRepository.delete(user);
    }

    // === 내부 헬퍼 ===

    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id=" + userId));
    }
}
