package com.example.DDD.application.service;

import com.example.DDD.application.dto.request.CommentEditRequest;
import com.example.DDD.application.dto.response.CommentResponse;
import com.example.DDD.domain.comment.Comment;
import com.example.DDD.domain.comment.CommentRepository;
import com.example.DDD.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;

    /**
     * 댓글 수정
     * - 작성자 검증 로직은 Comment 엔티티에 위임
     */
    @Transactional
    public CommentResponse editComment(Long commentId, CommentEditRequest request) {
        Comment comment = findCommentById(commentId);
        User requester = userService.findUserById(request.requesterId());

        comment.edit(request.content(), requester);

        return CommentResponse.from(comment);
    }

    // === 내부 헬퍼 ===

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다. id=" + commentId));
    }
}
