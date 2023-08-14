package br.com.compass.Desafio3.service.impl;

import br.com.compass.Desafio3.DTO.CommentDto;
import br.com.compass.Desafio3.entity.Comment;
import br.com.compass.Desafio3.entity.History;
import br.com.compass.Desafio3.entity.Post;
import br.com.compass.Desafio3.repository.CommentRepository;
import br.com.compass.Desafio3.repository.HistoryRepository;
import br.com.compass.Desafio3.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PostServiceImplTest {

    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private HistoryRepository historyRepository;

    @Test
    public void testQueryPostsAsync() throws ExecutionException, InterruptedException {
        Post[] postsArray = new Post[2];
        when(restTemplate.getForObject(anyString(), eq(Post[].class))).thenReturn(postsArray);

        CompletableFuture<List<Post>> resultFuture = postService.queryPostsAsync();

        List<Post> result = resultFuture.join();
        assertEquals(0, result.size());
    }

    @Test
    public void testProcessPostAsync() throws ExecutionException, InterruptedException {
        Long postId = 1L;

        when(postRepository.save(any())).thenReturn(new Post());
        when(commentRepository.saveAll(any())).thenReturn(Collections.emptyList());
        when(historyRepository.save(any())).thenReturn(new History());

        CompletableFuture<Post> resultFuture = postService.processPostAsync(postId);
        Post result = resultFuture.get();

        assertNotNull(result);
    }

    @Test
    public void testDisablePostAsync() throws ExecutionException, InterruptedException {
        Long postId = 1L;

        when(postRepository.save(any())).thenReturn(new Post());
        when(commentRepository.saveAll(any())).thenReturn(Collections.emptyList());
        when(historyRepository.save(any())).thenReturn(new History());

        CompletableFuture<Post> resultFuture = postService.disablePostAsync(postId);
        Post result = resultFuture.get();

        assertNotNull(result);
    }
}