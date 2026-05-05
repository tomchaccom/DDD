package com.example.DDD.domain.post;

import com.example.DDD.domain.comment.Comment;
import com.example.DDD.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private PostTitle title;

    @Embedded
    private PostContent content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    private Post(PostTitle title, PostContent content, User author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    // === Static Factory Method ===

    public static Post create(String title, String content, User author) {
        return new Post(PostTitle.of(title), PostContent.of(content), author);
    }

    // === 도메인 로직 ===

    /**
     * 게시글 수정 — 작성자 본인만 수정 가능
     */
    public void edit(String newTitle, String newContent, User requester) {
        validateAuthor(requester);
        this.title = PostTitle.of(newTitle);
        this.content = PostContent.of(newContent);
    }

    /**
     * 댓글 추가 — 양방향 연관관계 편의 메서드
     * Post가 Comment의 생성을 직접 책임 (Aggregate Root 역할)
     */
    public Comment addComment(String content, User commenter) {
        Comment comment = Comment.create(content, this, commenter);
        this.comments.add(comment);
        return comment;
    }

    /**
     * 댓글 삭제 — 댓글 작성자 또는 게시글 작성자만 삭제 가능
     */
    public void removeComment(Long commentId, User requester) {
        Comment comment = findComment(commentId);
        if (!comment.isWrittenBy(requester) && !this.isWrittenBy(requester)) {
            throw new IllegalArgumentException("댓글 삭제 권한이 없습니다.");
        }
        this.comments.remove(comment);
    }

    /**
     * 댓글 목록 조회 — 불변 리스트로 반환 (캡슐화 보호)
     */
    public List<Comment> getComments() {
        return Collections.unmodifiableList(comments);
    }

    /**
     * 해당 사용자가 작성한 게시글인지 확인
     */
    public boolean isWrittenBy(User user) {
        return this.author.isSameUser(user);
    }

    /**
     * 현재 댓글 수 조회
     */
    public int getCommentCount() {
        return this.comments.size();
    }

    /**
     * 댓글 수정 — Aggregate Root를 통해 Comment.edit()에 위임
     */
    public void editComment(Long commentId, String newContent, User requester) {
        Comment targetComment = findComment(commentId);
        targetComment.edit(newContent, requester);
    }

    /**
     * Aggregate 내부에서 댓글 조회
     */
    private Comment findComment(Long commentId) {
        return this.comments.stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 게시글에 존재하지 않는 댓글입니다. commentId=" + commentId));
    }

    // === 내부 검증 ===

    private void validateAuthor(User requester) {
        if (!isWrittenBy(requester)) {
            throw new IllegalArgumentException("게시글 작성자만 수정할 수 있습니다.");
        }
    }
}
