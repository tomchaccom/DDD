package com.example.DDD.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EmailTest {

    // ====================================================
    // 분기 1: value == null → IllegalArgumentException
    // ====================================================

    @Nested
    @DisplayName("null 검증")
    class NullValidation {

        @Test
        @DisplayName("null 입력 시 예외 발생")
        void shouldThrowException_whenNull() {
            assertThatThrownBy(() -> Email.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이메일은 필수 입력값입니다.");
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
            assertThatThrownBy(() -> Email.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이메일은 필수 입력값입니다.");
        }

        @Test
        @DisplayName("공백만 있는 문자열 입력 시 예외 발생")
        void shouldThrowException_whenBlank() {
            assertThatThrownBy(() -> Email.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이메일은 필수 입력값입니다.");
        }
    }

    // ====================================================
    // 분기 3: !EMAIL_PATTERN.matcher(value).matches()
    // (조건 커버리지: null 아니고, blank 아닌데 형식이 틀린 경우)
    // ====================================================

    @Nested
    @DisplayName("이메일 형식 검증")
    class FormatValidation {

        @Test
        @DisplayName("@ 없는 이메일은 예외 발생")
        void shouldThrowException_whenNoAtSign() {
            assertThatThrownBy(() -> Email.of("testexample.com"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("올바른 이메일 형식이 아닙니다");
        }

        @Test
        @DisplayName("도메인 없는 이메일은 예외 발생")
        void shouldThrowException_whenNoDomain() {
            assertThatThrownBy(() -> Email.of("test@"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("올바른 이메일 형식이 아닙니다");
        }

        @Test
        @DisplayName("TLD 1자인 이메일은 예외 발생")
        void shouldThrowException_whenTldTooShort() {
            assertThatThrownBy(() -> Email.of("test@example.c"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("올바른 이메일 형식이 아닙니다");
        }

        @Test
        @DisplayName("로컬 파트 없는 이메일은 예외 발생")
        void shouldThrowException_whenNoLocalPart() {
            assertThatThrownBy(() -> Email.of("@example.com"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("올바른 이메일 형식이 아닙니다");
        }
    }

    // ====================================================
    // 분기 4: 모든 검증 통과 → 정상 생성
    // ====================================================

    @Nested
    @DisplayName("정상 생성")
    class SuccessfulCreation {

        @Test
        @DisplayName("유효한 이메일로 정상 생성")
        void shouldCreate_whenValidEmail() {
            Email email = Email.of("test@example.com");

            assertThat(email.getValue()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("TLD 2자인 이메일도 정상 생성 (경계값)")
        void shouldCreate_whenTldMinLength() {
            Email email = Email.of("test@example.co");

            assertThat(email.getValue()).isEqualTo("test@example.co");
        }

        @Test
        @DisplayName("특수문자(+_.-) 포함 이메일 정상 생성")
        void shouldCreate_whenSpecialCharsInLocal() {
            Email email = Email.of("user+tag_name.last@example.com");

            assertThat(email.getValue()).isEqualTo("user+tag_name.last@example.com");
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
            Email email1 = Email.of("test@example.com");
            Email email2 = Email.of("test@example.com");

            assertThat(email1).isEqualTo(email2);
            assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 동등하지 않다")
        void shouldNotBeEqual_whenDifferentValue() {
            Email email1 = Email.of("test@example.com");
            Email email2 = Email.of("other@example.com");

            assertThat(email1).isNotEqualTo(email2);
        }
    }
}
