package com.example.DDD.domain.post;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글 제목 값 객체 (Value Object)
 * - 불변 객체로 설계
 * - 생성 시 길이 검증 (1~200자)
 * - 동등성은 값(value)으로 비교
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostTitle {

    private static final int MAX_LENGTH = 200;

    @Column(name = "title", nullable = false, length = 200)
    private String value;

    private PostTitle(String value) {
        this.value = value;
    }

    public static PostTitle of(String value) {
        validate(value);
        return new PostTitle(value.trim());
    }

    private static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("게시글 제목은 필수 입력값입니다.");
        }
        if (value.trim().length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("게시글 제목은 %d자를 초과할 수 없습니다.", MAX_LENGTH));
        }
    }
}
