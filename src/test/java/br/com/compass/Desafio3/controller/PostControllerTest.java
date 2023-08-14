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
import static org.mockito.ArgumentMatchers.*;
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
    public void testQueryPosts() throws ExecutionException, InterruptedException {
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
    public void testProcessPost() throws ExecutionException, InterruptedException {
        Long postId = 1L;
        Post processedPost = new Post();

        when(postService.processPostAsync(postId)).thenReturn(CompletableFuture.completedFuture(processedPost));
        when(modelMapper.map(any(Post.class), eq(PostDto.class))).thenReturn(new PostDto());

        ResponseEntity<PostDto> responseEntity = ResponseEntity.ok(new PostDto());
        when(modelMapper.map(any(), eq(ResponseEntity.class))).thenReturn(responseEntity);

        ResponseEntity<PostDto> response = postController.processPost(postId).get();

        verify(postService).processPostAsync(eq(postId));
        verify(modelMapper).map(processedPost, PostDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testDisablePost() {
        Long postId = 1L;
        Post post = new Post();
        PostDto postDto = new PostDto();

        when(postService.disablePostAsync(postId)).thenReturn(CompletableFuture.completedFuture(post));
        when(modelMapper.map(eq(post), eq(PostDto.class))).thenReturn(postDto);

        ResponseEntity<PostDto> response = postController.disablePost(postId);

        verify(postService).disablePostAsync(eq(postId));
        verify(modelMapper).map(eq(post), eq(PostDto.class));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(postDto, response.getBody());
    }

    @Test
    public void testReprocessPost() {
        Long postId = 1L;
        Post post = new Post();
        PostDto postDto = new PostDto();

        when(postService.reprocessPostAsync(postId)).thenReturn(CompletableFuture.completedFuture(post));
        when(modelMapper.map(eq(post), eq(PostDto.class))).thenReturn(postDto);

        ResponseEntity<PostDto> response = postController.reprocessPost(postId);

        verify(postService).reprocessPostAsync(eq(postId));
        verify(modelMapper).map(eq(post), eq(PostDto.class));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(postDto, response.getBody());
    }
}