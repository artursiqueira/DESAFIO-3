package br.com.compass.Desafio3.controller;

import br.com.compass.Desafio3.DTO.PostDto;
import br.com.compass.Desafio3.entity.Comment;
import br.com.compass.Desafio3.entity.Post;
import br.com.compass.Desafio3.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    private final ModelMapper modelMapper;

    public PostController(PostService postService, ModelMapper modelMapper) {
        this.postService = postService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<List<PostDto>> queryPosts() {
        CompletableFuture<List<Post>> postsFuture = postService.queryPostsAsync();

        try {
            List<Post> posts = postsFuture.get();
            List<PostDto> postDtos = posts.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(postDtos);
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{postId}")
    public ResponseEntity<PostDto> processPost(@PathVariable Long postId, @RequestBody Post requestBody) {
        if (postId < 1 || postId > 100) {
            return ResponseEntity.badRequest().build();
        }

        Post existingPost = postService.findPostById(postId);
        if (existingPost != null) {
            return ResponseEntity.badRequest().build();
        }

        if (!postId.equals(requestBody.getId())) {
            return ResponseEntity.badRequest().build();
        }

        CompletableFuture<Post> processedPostFuture = postService.processPostAsync(postId, requestBody);

        try {
            Post processedPost = processedPostFuture.get();
            if (processedPost == null) {
                return ResponseEntity.notFound().build();
            }

            PostDto processedPostDto = convertToDto(processedPost);
            return ResponseEntity.ok(processedPostDto);
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("/{postId}")
    public ResponseEntity<PostDto> disablePost(@PathVariable Long postId) {
        if (postId < 1 || postId > 100) {
            return ResponseEntity.badRequest().build();
        }

        CompletableFuture<Post> postFuture = postService.disablePostAsync(postId);

        try {
            Post post = postFuture.get();
            PostDto postDto = convertToDto(post);
            return ResponseEntity.ok(postDto);
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDto> reprocessPost(@PathVariable Long postId) {
        if (postId < 1 || postId > 100) {
            return ResponseEntity.badRequest().build();
        }

        CompletableFuture<Post> processedPostFuture = postService.reprocessPostAsync(postId);

        try {
            Post processedPost = processedPostFuture.get();
            if (processedPost == null) {
                return ResponseEntity.notFound().build();
            }

            PostDto processedPostDto = convertToDto(processedPost);
            return ResponseEntity.ok(processedPostDto);
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long postId) {
        Post post = postService.findPostById(postId);

        if (post != null) {
            PostDto postDto = convertToDto(post);
            return ResponseEntity.ok(postDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<Comment[]> getCommentsForPost(@PathVariable Long postId) {
        Comment[] comments = postService.findCommentsForPost(postId);
        return ResponseEntity.ok(comments);
    }
    @GetMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long commentId) {
        Comment comment = postService.findCommentById(commentId);
        return ResponseEntity.ok(comment);
    }
    private PostDto convertToDto(Post post) {
        return modelMapper.map(post, PostDto.class);
    }
}

