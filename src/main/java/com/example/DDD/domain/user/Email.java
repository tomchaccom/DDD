package com.example.DDD.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

/**
 * 이메일 값 객체 (Value Object)
 * - 불변 객체로 설계
 * - 생성 시 이메일 형식 검증
 * - 동등성은 값(value)으로 비교
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Email {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String value;

    private Email(String value) {
        this.value = value;
    }

    public static Email of(String value) {
        validate(value);
        return new Email(value);
    }

    private static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("이메일은 필수 입력값입니다.");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다: " + value);
        }
    }
}
