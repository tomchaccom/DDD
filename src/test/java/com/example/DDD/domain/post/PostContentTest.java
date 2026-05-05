package com.example.DDD.domain.post;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PostContentTest {

    // ====================================================
    // 분기 1: value == null → IllegalArgumentException
    // ====================================================

    @Nested
    @DisplayName("null 검증")
    class NullValidation {

        @Test
        @DisplayName("null 입력 시 예외 발생")
        void shouldThrowException_whenNull() {
            assertThatThrownBy(() -> PostContent.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("게시글 내용은 필수 입력값입니다.");
        }
    }

    // ====================================================
    // 분기 2: value.isBlank() → IllegalArgumentException
    // (조건 커버리지: null이 아닌데 blank인 경우)
    // ====================================================

    @Nested
    @DisplayName("빈 값 검증")
    class BlankValidation {

        @Test
        @DisplayName("빈 문자열 입력 시 예외 발생")
        void shouldThrowException_whenEmpty() {
            assertThatThrownBy(() -> PostContent.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("게시글 내용은 필수 입력값입니다.");
        }

        @Test
        @DisplayName("공백만 있는 문자열 입력 시 예외 발생")
        void shouldThrowException_whenBlank() {
            assertThatThrownBy(() -> PostContent.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("게시글 내용은 필수 입력값입니다.");
        }
    }

    // ====================================================
    // 분기 3: value.length() > MAX_LENGTH (10,000자 초과)
    // (조건 커버리지: null 아니고, blank 아닌데 길이 초과)
    // ====================================================

    @Nested
    @DisplayName("최대 길이 검증 (MAX_LENGTH=10,000)")
    class MaxLengthValidation {

        @Test
        @DisplayName("10,000자 입력 시 정상 생성 (경계값: MAX)")
        void shouldCreate_whenExactlyMaxLength() {
            String tenThousandChars = "a".repeat(10_000);
            PostContent content = PostContent.of(tenThousandChars);

            assertThat(content.getValue()).isEqualTo(tenThousandChars);
        }

        @Test
        @DisplayName("10,001자 입력 시 예외 발생 (경계값: MAX+1)")
        void shouldThrowException_whenExceedsMaxLength() {
            String tenThousandOneChars = "a".repeat(10_001);

            assertThatThrownBy(() -> PostContent.of(tenThousandOneChars))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("10000자를 초과할 수 없습니다");
        }
    }

    // ====================================================
    // 분기 4: 모든 검증 통과 → 정상 생성
    // ====================================================

    @Nested
    @DisplayName("정상 생성")
    class SuccessfulCreation {

        @Test
        @DisplayName("유효한 내용 정상 생성")
        void shouldCreate_whenValid() {
            PostContent content = PostContent.of("테스트 게시글 내용입니다.");

            assertThat(content.getValue()).isEqualTo("테스트 게시글 내용입니다.");
        }

        @Test
        @DisplayName("1자 내용 정상 생성 (경계값: 최소)")
        void shouldCreate_whenSingleChar() {
            PostContent content = PostContent.of("a");

            assertThat(content.getValue()).isEqualTo("a");
        }
    }

    // ====================================================
    // 값 객체 동등성 검증 (@EqualsAndHashCode)
    // ====================================================

    @Nested
    @DisplayName("동등성 검증")
    class Equality {

        @Test
        @DisplayName("같은 값이면 동등하다")
        void shouldBeEqual_whenSameValue() {
            PostContent content1 = PostContent.of("내용");
            PostContent content2 = PostContent.of("내용");

            assertThat(content1).isEqualTo(content2);
            assertThat(content1.hashCode()).isEqualTo(content2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 동등하지 않다")
        void shouldNotBeEqual_whenDifferentValue() {
            PostContent content1 = PostContent.of("내용1");
            PostContent content2 = PostContent.of("내용2");

            assertThat(content1).isNotEqualTo(content2);
        }
    }
}
