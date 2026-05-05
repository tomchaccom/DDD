package com.example.DDD.presentation;

import com.example.DDD.application.dto.request.CommentEditRequest;
import com.example.DDD.application.dto.response.CommentResponse;
import com.example.DDD.application.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> editComment(
            @PathVariable Long commentId,
            @RequestBody CommentEditRequest request
    ) {
        return ResponseEntity.ok(commentService.editComment(commentId, request));
    }
}
