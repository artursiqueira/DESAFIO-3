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
        try {
            CompletableFuture<List<Post>> postsFuture = postService.queryPostsAsync();
            CompletableFuture<List<PostDto>> postDtosFuture = postsFuture.thenApplyAsync(posts -> {
                return posts.stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());
            });
            List<PostDto> postDtos = postDtosFuture.get();
            return ResponseEntity.ok(postDtos);
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{postId}")
    public CompletableFuture<ResponseEntity<PostDto>> processPost(@PathVariable Long postId) {
        if (postId >= 1 && postId <= 100) {
            CompletableFuture<Post> processedPostFuture = postService.processPostAsync(postId);

            return processedPostFuture.thenApply(processedPost -> {
                if (processedPost == null) {
                    return ResponseEntity.notFound().build();
                }

                PostDto processedPostDto = convertToDto(processedPost);
                return ResponseEntity.ok(processedPostDto);
            });
        } else {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().build());
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
    public ResponseEntity<CompletableFuture<PostDto>> getPostById(@PathVariable Long postId) {
        CompletableFuture<Post> postFuture = postService.findPostByIdAsync(postId);

        return ResponseEntity.ok(postFuture.thenApply(post -> {
            if (post != null) {
                return convertToDto(post);
            } else {
                return null;
            }
        }));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<CompletableFuture<Comment[]>> getCommentsForPost(@PathVariable Long postId) {
        CompletableFuture<Comment[]> commentsFuture = postService.findCommentsForPostAsync(postId);
        return ResponseEntity.ok(commentsFuture);
    }
    @GetMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CompletableFuture<Comment>> getCommentById(@PathVariable Long commentId) {
        CompletableFuture<Comment> commentFuture = postService.findCommentByIdAsync(commentId);

        return ResponseEntity.ok(commentFuture);
    }
    private PostDto convertToDto(Post post) {
        return modelMapper.map(post, PostDto.class);
    }
}

