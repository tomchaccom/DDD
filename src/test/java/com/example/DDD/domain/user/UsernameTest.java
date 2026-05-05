package com.example.DDD.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UsernameTest {

    // ====================================================
    // 분기 1: value == null → IllegalArgumentException
    // ====================================================

    @Nested
    @DisplayName("null 검증")
    class NullValidation {

        @Test
        @DisplayName("null 입력 시 예외 발생")
        void shouldThrowException_whenNull() {
            assertThatThrownBy(() -> Username.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("유저네임은 필수 입력값입니다.");
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
            assertThatThrownBy(() -> Username.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("유저네임은 필수 입력값입니다.");
        }

        @Test
        @DisplayName("공백만 있는 문자열 입력 시 예외 발생")
        void shouldThrowException_whenBlank() {
            assertThatThrownBy(() -> Username.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("유저네임은 필수 입력값입니다.");
        }
    }

    // ====================================================
    // 분기 3: trimmed.length() < MIN_LENGTH (2자 미만)
    // (조건 커버리지: null 아니고, blank 아닌데 길이가 짧은 경우)
    // ====================================================

    @Nested
    @DisplayName("최소 길이 검증 (MIN_LENGTH=2)")
    class MinLengthValidation {

        @Test
        @DisplayName("1자 입력 시 예외 발생 (경계값: MIN-1)")
        void shouldThrowException_whenOneChar() {
            assertThatThrownBy(() -> Username.of("a"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("2자 이상");
        }

        @Test
        @DisplayName("앞뒤 공백 제거 후 1자이면 예외 발생")
        void shouldThrowException_whenOneCharAfterTrim() {
            assertThatThrownBy(() -> Username.of("  a  "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("2자 이상");
        }

        @Test
        @DisplayName("2자 입력 시 정상 생성 (경계값: MIN)")
        void shouldCreate_whenExactlyMinLength() {
            Username username = Username.of("ab");

            assertThat(username.getValue()).isEqualTo("ab");
        }
    }

    // ====================================================
    // 분기 4: trimmed.length() > MAX_LENGTH (50자 초과)
    // (조건 커버리지: 길이가 긴 경우만 true)
    // ====================================================

    @Nested
    @DisplayName("최대 길이 검증 (MAX_LENGTH=50)")
    class MaxLengthValidation {

        @Test
        @DisplayName("50자 입력 시 정상 생성 (경계값: MAX)")
        void shouldCreate_whenExactlyMaxLength() {
            String fiftyChars = "a".repeat(50);
            Username username = Username.of(fiftyChars);

            assertThat(username.getValue()).isEqualTo(fiftyChars);
        }

        @Test
        @DisplayName("51자 입력 시 예외 발생 (경계값: MAX+1)")
        void shouldThrowException_whenExceedsMaxLength() {
            String fiftyOneChars = "a".repeat(51);

            assertThatThrownBy(() -> Username.of(fiftyOneChars))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("50자 이하");
        }
    }

    // ====================================================
    // 분기 5: 모든 검증 통과 → 정상 생성 + trim 처리
    // ====================================================

    @Nested
    @DisplayName("정상 생성 및 trim 처리")
    class SuccessfulCreation {

        @Test
        @DisplayName("유효한 유저네임 정상 생성")
        void shouldCreate_whenValid() {
            Username username = Username.of("testuser");

            assertThat(username.getValue()).isEqualTo("testuser");
        }

        @Test
        @DisplayName("앞뒤 공백이 trim 처리된다")
        void shouldTrimWhitespace() {
            Username username = Username.of("  testuser  ");

            assertThat(username.getValue()).isEqualTo("testuser");
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
            Username username1 = Username.of("testuser");
            Username username2 = Username.of("testuser");

            assertThat(username1).isEqualTo(username2);
            assertThat(username1.hashCode()).isEqualTo(username2.hashCode());
        }

        @Test
        @DisplayName("trim 후 같은 값이면 동등하다")
        void shouldBeEqual_whenSameValueAfterTrim() {
            Username username1 = Username.of("testuser");
            Username username2 = Username.of("  testuser  ");

            assertThat(username1).isEqualTo(username2);
        }

        @Test
        @DisplayName("다른 값이면 동등하지 않다")
        void shouldNotBeEqual_whenDifferentValue() {
            Username username1 = Username.of("user1");
            Username username2 = Username.of("user2");

            assertThat(username1).isNotEqualTo(username2);
        }
    }
}
