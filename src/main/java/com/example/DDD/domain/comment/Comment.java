package com.example.DDD.domain.comment;

import com.example.DDD.domain.post.Post;
import com.example.DDD.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    private Comment(String content, Post post, User author) {
        validateContent(content);
        this.content = content;
        this.post = post;
        this.author = author;
    }

    // === Static Factory Method ===

    /**
     * 댓글 생성 — Post.addComment()를 통해 생성하는 것을 권장합니다.
     * Post(Aggregate Root)가 댓글의 생명주기를 관리합니다.
     */
    public static Comment create(String content, Post post, User author) {
        return new Comment(content, post, author);
    }

    // === 도메인 로직 ===

    /**
     * 댓글 수정 — 작성자 본인만 수정 가능
     */
    public void edit(String newContent, User requester) {
        validateAuthor(requester);
        validateContent(newContent);
        this.content = newContent;
    }

    /**
     * 해당 사용자가 작성한 댓글인지 확인
     */
    public boolean isWrittenBy(User user) {
        return this.author.isSameUser(user);
    }

    // === 내부 검증 ===

    private void validateAuthor(User requester) {
        if (!isWrittenBy(requester)) {
            throw new IllegalArgumentException("댓글 작성자만 수정할 수 있습니다.");
        }
    }

    private static void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("댓글 내용은 필수 입력값입니다.");
        }
        if (content.length() > 1_000) {
            throw new IllegalArgumentException("댓글은 1,000자를 초과할 수 없습니다.");
        }
    }
}
