package com.example.DDD.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 유저네임 값 객체 (Value Object)
 * - 불변 객체로 설계
 * - 생성 시 길이 및 공백 검증
 * - 동등성은 값(value)으로 비교
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Username {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 50;

    @Column(name = "username", nullable = false, length = 50)
    private String value;

    private Username(String value) {
        this.value = value;
    }

    public static Username of(String value) {
        validate(value);
        return new Username(value.trim());
    }

    private static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("유저네임은 필수 입력값입니다.");
        }
        String trimmed = value.trim();
        if (trimmed.length() < MIN_LENGTH || trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("유저네임은 %d자 이상 %d자 이하여야 합니다.", MIN_LENGTH, MAX_LENGTH));
        }
    }
}
