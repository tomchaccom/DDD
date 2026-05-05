package com.example.DDD.application.service;

import com.example.DDD.application.dto.request.CommentCreateRequest;
import com.example.DDD.application.dto.request.CommentEditRequest;
import com.example.DDD.application.dto.request.PostCreateRequest;
import com.example.DDD.application.dto.request.PostEditRequest;
import com.example.DDD.application.dto.response.CommentResponse;
import com.example.DDD.application.dto.response.PostResponse;
import com.example.DDD.domain.comment.Comment;
import com.example.DDD.domain.post.Post;
import com.example.DDD.domain.post.PostRepository;
import com.example.DDD.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * [Command] 게시글 쓰기 전용 서비스
 * - 모든 변경 작업은 Post(Aggregate Root)를 통해 수행
 * - 댓글 생성/수정/삭제도 Post를 통해 처리 (Aggregate 경계 일관성)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PostCommandService {

    private final PostRepository postRepository;
    private final UserCommandService userCommandService;

    /**
     * 게시글 생성
     */
    public PostResponse createPost(PostCreateRequest request) {
        User author = userCommandService.findUserById(request.authorId());
        Post post = Post.create(request.title(), request.content(), author);
        Post savedPost = postRepository.save(post);
        return PostResponse.from(savedPost);
    }

    /**
     * 게시글 수정 — 작성자 검증은 Post 엔티티에 위임
     */
    public PostResponse editPost(Long postId, PostEditRequest request) {
        Post post = findPostById(postId);
        User requester = userCommandService.findUserById(request.requesterId());

        post.edit(request.title(), request.content(), requester);

        return PostResponse.from(post);
    }

    /**
     * 게시글 삭제 — 작성자 본인만 삭제 가능
     */
    public void deletePost(Long postId, Long requesterId) {
        Post post = findPostById(postId);
        User requester = userCommandService.findUserById(requesterId);

        if (!post.isWrittenBy(requester)) {
            throw new IllegalArgumentException("게시글 작성자만 삭제할 수 있습니다.");
        }

        postRepository.delete(post);
    }

    /**
     * 댓글 추가 — Post(Aggregate Root).addComment()를 통해 생성
     */
    public CommentResponse addComment(Long postId, CommentCreateRequest request) {
        Post post = findPostById(postId);
        User commenter = userCommandService.findUserById(request.commenterId());

        Comment comment = post.addComment(request.content(), commenter);

        return CommentResponse.from(comment);
    }

    /**
     * 댓글 수정 — Post(Aggregate Root).editComment()를 통해 Comment.edit()에 위임
     */
    public CommentResponse editComment(Long postId, Long commentId, CommentEditRequest request) {
        Post post = findPostById(postId);
        User requester = userCommandService.findUserById(request.requesterId());

        post.editComment(commentId, request.content(), requester);

        // 수정된 댓글을 찾아서 응답 반환
        Comment editedComment = post.getComments().stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst()
                .orElseThrow();

        return CommentResponse.from(editedComment);
    }

    /**
     * 댓글 삭제 — Post(Aggregate Root).removeComment()를 통해 삭제
     */
    public void removeComment(Long postId, Long commentId, Long requesterId) {
        Post post = findPostById(postId);
        User requester = userCommandService.findUserById(requesterId);

        post.removeComment(commentId, requester);
    }

    // === 내부 헬퍼 ===

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다. id=" + postId));
    }
}
