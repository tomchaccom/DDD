package com.example.DDD.application.service;

import com.example.DDD.application.dto.request.CommentCreateRequest;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    /**
     * 게시글 생성
     */
    @Transactional
    public PostResponse createPost(PostCreateRequest request) {
        User author = userService.findUserById(request.authorId());
        Post post = Post.create(request.title(), request.content(), author);
        Post savedPost = postRepository.save(post);
        return PostResponse.from(savedPost);
    }

    /**
     * 게시글 단건 조회 (댓글 포함)
     */
    public PostResponse getPost(Long postId) {
        Post post = findPostById(postId);
        return PostResponse.from(post);
    }

    /**
     * 게시글 목록 조회 (요약 — 댓글 미포함)
     */
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll().stream()
                .map(PostResponse::summaryFrom)
                .toList();
    }

    /**
     * 게시글 수정
     * - 작성자 검증 로직은 Post 엔티티에 위임
     */
    @Transactional
    public PostResponse editPost(Long postId, PostEditRequest request) {
        Post post = findPostById(postId);
        User requester = userService.findUserById(request.requesterId());

        post.edit(request.title(), request.content(), requester);

        return PostResponse.from(post);
    }

    /**
     * 게시글 삭제
     * - 작성자 본인만 삭제 가능
     */
    @Transactional
    public void deletePost(Long postId, Long requesterId) {
        Post post = findPostById(postId);
        User requester = userService.findUserById(requesterId);

        if (!post.isWrittenBy(requester)) {
            throw new IllegalArgumentException("게시글 작성자만 삭제할 수 있습니다.");
        }

        postRepository.delete(post);
    }

    /**
     * 댓글 추가
     * - Post(Aggregate Root)의 addComment()를 통해 생성
     */
    @Transactional
    public CommentResponse addComment(Long postId, CommentCreateRequest request) {
        Post post = findPostById(postId);
        User commenter = userService.findUserById(request.commenterId());

        Comment comment = post.addComment(request.content(), commenter);

        return CommentResponse.from(comment);
    }

    /**
     * 댓글 삭제
     * - Post(Aggregate Root)의 removeComment()를 통해 삭제
     * - 댓글 작성자 또는 게시글 작성자만 삭제 가능 (엔티티에서 검증)
     */
    @Transactional
    public void removeComment(Long postId, Long commentId, Long requesterId) {
        Post post = findPostById(postId);
        User requester = userService.findUserById(requesterId);

        Comment targetComment = post.getComments().stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 게시글에 존재하지 않는 댓글입니다. commentId=" + commentId));

        post.removeComment(targetComment, requester);
    }

    // === 내부 헬퍼 ===

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다. id=" + postId));
    }
}
