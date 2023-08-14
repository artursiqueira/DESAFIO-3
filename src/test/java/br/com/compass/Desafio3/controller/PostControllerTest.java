package br.com.compass.Desafio3.controller;

import br.com.compass.Desafio3.DTO.PostDto;
import br.com.compass.Desafio3.entity.Post;
import br.com.compass.Desafio3.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class PostControllerTest {

    @InjectMocks
    private PostController postController;

    @Mock
    private PostService postService;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testQueryPosts(){
        List<Post> mockPosts = new ArrayList<>();
        when(postService.queryPostsAsync()).thenReturn(CompletableFuture.completedFuture(mockPosts));
        when(modelMapper.map(any(Post.class), eq(PostDto.class))).thenReturn(new PostDto());

        ResponseEntity<List<PostDto>> response = postController.queryPosts();

        verify(postService).queryPostsAsync();
        verify(modelMapper, times(mockPosts.size())).map(any(Post.class), eq(PostDto.class));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPosts.size(), Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    public void testProcessPost(){
        Long postId = 1L;
        Post requestBody = new Post();
        Post processedPost = new Post();

        when(postService.findPostById(postId)).thenReturn(null);
        when(postService.processPostAsync(postId, requestBody)).thenReturn(CompletableFuture.completedFuture(processedPost));
        when(modelMapper.map(any(Post.class), eq(PostDto.class))).thenReturn(new PostDto());

        ResponseEntity<PostDto> response = postController.processPost(postId, requestBody);

        verify(postService).findPostById(postId);
        verify(postService).processPostAsync(eq(postId), eq(requestBody));
        verify(modelMapper).map(processedPost, PostDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testDisablePost(){
        Long postId = 1L;
        Post post = new Post();

        when(postService.disablePostAsync(postId)).thenReturn(CompletableFuture.completedFuture(post));
        when(modelMapper.map(any(Post.class), eq(PostDto.class))).thenReturn(new PostDto());

        ResponseEntity<PostDto> response = postController.disablePost(postId);

        verify(postService).disablePostAsync(postId);
        verify(modelMapper).map(post, PostDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testReprocessPost(){
        Long postId = 1L;
        Post post = new Post();

        when(postService.reprocessPostAsync(postId)).thenReturn(CompletableFuture.completedFuture(post));
        when(modelMapper.map(any(Post.class), eq(PostDto.class))).thenReturn(new PostDto());

        ResponseEntity<PostDto> response = postController.reprocessPost(postId);

        verify(postService).reprocessPostAsync(postId);
        verify(modelMapper).map(post, PostDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}