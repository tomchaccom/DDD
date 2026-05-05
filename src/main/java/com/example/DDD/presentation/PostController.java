package com.example.DDD.presentation;

import com.example.DDD.application.dto.request.CommentCreateRequest;
import com.example.DDD.application.dto.request.PostCreateRequest;
import com.example.DDD.application.dto.request.PostEditRequest;
import com.example.DDD.application.dto.response.CommentResponse;
import com.example.DDD.application.dto.response.PostResponse;
import com.example.DDD.application.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostCreateRequest request) {
        PostResponse response = postService.createPost(request);
        return ResponseEntity.created(URI.create("/api/posts/" + response.id()))
                .body(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponse> editPost(
            @PathVariable Long postId,
            @RequestBody PostEditRequest request
    ) {
        return ResponseEntity.ok(postService.editPost(postId, request));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @RequestParam Long requesterId
    ) {
        postService.deletePost(postId, requesterId);
        return ResponseEntity.noContent().build();
    }

    // === 댓글 (Post Aggregate Root 하위 리소스) ===

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long postId,
            @RequestBody CommentCreateRequest request
    ) {
        CommentResponse response = postService.addComment(postId, request);
        return ResponseEntity.created(
                URI.create("/api/posts/" + postId + "/comments/" + response.id())
        ).body(response);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> removeComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestParam Long requesterId
    ) {
        postService.removeComment(postId, commentId, requesterId);
        return ResponseEntity.noContent().build();
    }
}
