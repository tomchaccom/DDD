package com.example.DDD.application.service;

import com.example.DDD.application.dto.response.UserResponse;
import com.example.DDD.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * [Query] 유저 읽기 전용 서비스
 * - readOnly 트랜잭션으로 성능 최적화
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserRepository userRepository;

    /**
     * 회원 단건 조회
     */
    public UserResponse getUser(Long userId) {
        return userRepository.findById(userId)
                .map(UserResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id=" + userId));
    }

    /**
     * 회원 목록 조회
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }
}
