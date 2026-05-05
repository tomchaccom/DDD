package com.example.DDD.presentation;

import com.example.DDD.application.dto.request.CommentCreateRequest;
import com.example.DDD.application.dto.request.CommentEditRequest;
import com.example.DDD.application.dto.request.PostCreateRequest;
import com.example.DDD.application.dto.request.PostEditRequest;
import com.example.DDD.application.dto.response.CommentResponse;
import com.example.DDD.application.dto.response.PostResponse;
import com.example.DDD.application.service.PostCommandService;
import com.example.DDD.application.service.PostQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostCommandService postCommandService;
    private final PostQueryService postQueryService;

    // === Query (읽기) ===

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.ok(postQueryService.getAllPosts());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postQueryService.getPost(postId));
    }

    // === Command (쓰기) ===

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostCreateRequest request) {
        PostResponse response = postCommandService.createPost(request);
        return ResponseEntity.created(URI.create("/api/posts/" + response.id()))
                .body(response);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponse> editPost(
            @PathVariable Long postId,
            @RequestBody PostEditRequest request
    ) {
        return ResponseEntity.ok(postCommandService.editPost(postId, request));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @RequestParam Long requesterId
    ) {
        postCommandService.deletePost(postId, requesterId);
        return ResponseEntity.noContent().build();
    }

    // === Command — 댓글 (Post Aggregate Root 하위 리소스) ===

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long postId,
            @RequestBody CommentCreateRequest request
    ) {
        CommentResponse response = postCommandService.addComment(postId, request);
        return ResponseEntity.created(
                URI.create("/api/posts/" + postId + "/comments/" + response.id())
        ).body(response);
    }

    @PatchMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponse> editComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentEditRequest request
    ) {
        return ResponseEntity.ok(postCommandService.editComment(postId, commentId, request));
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> removeComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestParam Long requesterId
    ) {
        postCommandService.removeComment(postId, commentId, requesterId);
        return ResponseEntity.noContent().build();
    }
}
