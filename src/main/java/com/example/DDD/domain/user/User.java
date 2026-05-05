package com.example.DDD.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Username username;

    @Embedded
    private Email email;

    private User(Username username, Email email) {
        this.username = username;
        this.email = email;
    }

    // === Static Factory Method ===

    public static User create(String username, String email) {
        return new User(Username.of(username), Email.of(email));
    }

    // === 도메인 로직 ===

    /**
     * 유저네임 변경
     * - 값 객체 내부에서 검증이 수행됨
     */
    public void changeUsername(String newUsername) {
        this.username = Username.of(newUsername);
    }

    /**
     * 이메일 변경
     * - 값 객체 내부에서 이메일 형식 검증이 수행됨
     */
    public void changeEmail(String newEmail) {
        this.email = Email.of(newEmail);
    }

    /**
     * 동일 사용자인지 확인 (엔티티 식별자 기반)
     */
    public boolean isSameUser(User other) {
        if (other == null || this.id == null) {
            return false;
        }
        return this.id.equals(other.getId());
    }
}
