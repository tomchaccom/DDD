package com.example.DDD.domain.post;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PostTitleTest {

    // ====================================================
    // 분기 1: value == null → IllegalArgumentException
    // ====================================================

    @Nested
    @DisplayName("null 검증")
    class NullValidation {

        @Test
        @DisplayName("null 입력 시 예외 발생")
        void shouldThrowException_whenNull() {
            assertThatThrownBy(() -> PostTitle.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("게시글 제목은 필수 입력값입니다.");
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
            assertThatThrownBy(() -> PostTitle.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("게시글 제목은 필수 입력값입니다.");
        }

        @Test
        @DisplayName("공백만 있는 문자열 입력 시 예외 발생")
        void shouldThrowException_whenBlank() {
            assertThatThrownBy(() -> PostTitle.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("게시글 제목은 필수 입력값입니다.");
        }
    }

    // ====================================================
    // 분기 3: value.trim().length() > MAX_LENGTH (200자 초과)
    // (조건 커버리지: null 아니고, blank 아닌데 길이 초과)
    // ====================================================

    @Nested
    @DisplayName("최대 길이 검증 (MAX_LENGTH=200)")
    class MaxLengthValidation {

        @Test
        @DisplayName("200자 입력 시 정상 생성 (경계값: MAX)")
        void shouldCreate_whenExactlyMaxLength() {
            String twoHundredChars = "a".repeat(200);
            PostTitle title = PostTitle.of(twoHundredChars);

            assertThat(title.getValue()).isEqualTo(twoHundredChars);
        }

        @Test
        @DisplayName("201자 입력 시 예외 발생 (경계값: MAX+1)")
        void shouldThrowException_whenExceedsMaxLength() {
            String twoHundredOneChars = "a".repeat(201);

            assertThatThrownBy(() -> PostTitle.of(twoHundredOneChars))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("200자를 초과할 수 없습니다");
        }

        @Test
        @DisplayName("앞뒤 공백 포함 200자 초과 시 trim 후 판단")
        void shouldCreate_whenExceedsMaxLengthButTrimmedFits() {
            // trim 전 202자, trim 후 200자 → 통과해야 함
            String padded = " " + "a".repeat(200) + " ";
            PostTitle title = PostTitle.of(padded);

            assertThat(title.getValue()).isEqualTo("a".repeat(200));
        }
    }

    // ====================================================
    // 분기 4: 모든 검증 통과 → 정상 생성 + trim 처리
    // ====================================================

    @Nested
    @DisplayName("정상 생성 및 trim 처리")
    class SuccessfulCreation {

        @Test
        @DisplayName("유효한 제목 정상 생성")
        void shouldCreate_whenValid() {
            PostTitle title = PostTitle.of("테스트 게시글 제목");

            assertThat(title.getValue()).isEqualTo("테스트 게시글 제목");
        }

        @Test
        @DisplayName("1자 제목 정상 생성 (경계값: 최소)")
        void shouldCreate_whenSingleChar() {
            PostTitle title = PostTitle.of("a");

            assertThat(title.getValue()).isEqualTo("a");
        }

        @Test
        @DisplayName("앞뒤 공백이 trim 처리된다")
        void shouldTrimWhitespace() {
            PostTitle title = PostTitle.of("  제목  ");

            assertThat(title.getValue()).isEqualTo("제목");
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
            PostTitle title1 = PostTitle.of("제목");
            PostTitle title2 = PostTitle.of("제목");

            assertThat(title1).isEqualTo(title2);
            assertThat(title1.hashCode()).isEqualTo(title2.hashCode());
        }

        @Test
        @DisplayName("trim 후 같은 값이면 동등하다")
        void shouldBeEqual_whenSameValueAfterTrim() {
            PostTitle title1 = PostTitle.of("제목");
            PostTitle title2 = PostTitle.of("  제목  ");

            assertThat(title1).isEqualTo(title2);
        }

        @Test
        @DisplayName("다른 값이면 동등하지 않다")
        void shouldNotBeEqual_whenDifferentValue() {
            PostTitle title1 = PostTitle.of("제목1");
            PostTitle title2 = PostTitle.of("제목2");

            assertThat(title1).isNotEqualTo(title2);
        }
    }
}
