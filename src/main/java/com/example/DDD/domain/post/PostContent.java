package com.example.DDD.domain.post;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글 내용 값 객체 (Value Object)
 * - 불변 객체로 설계
 * - 생성 시 빈 내용 검증 및 최대 길이 제한
 * - 동등성은 값(value)으로 비교
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostContent {

    private static final int MAX_LENGTH = 10_000;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String value;

    private PostContent(String value) {
        this.value = value;
    }

    public static PostContent of(String value) {
        validate(value);
        return new PostContent(value);
    }

    private static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("게시글 내용은 필수 입력값입니다.");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("게시글 내용은 %d자를 초과할 수 없습니다.", MAX_LENGTH));
        }
    }
}
