package br.com.compass.Desafio3.service.impl;

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
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

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
    public void testQueryPostsAsync() {
        Post[] postsArray = new Post[2];

        when(restTemplate.getForObject(anyString(), eq(Post[].class))).thenReturn(postsArray);

        CompletableFuture<List<Post>> resultFuture = postService.queryPostsAsync();

        List<Post> result = resultFuture.join();
        assertEquals(100, result.size());
    }

    @Test
    public void testProcessPostAsync() {
        Long postId = 1L;
        Post requestBody = new Post();

        when(postRepository.save(any())).thenReturn(requestBody);
        when(commentRepository.saveAll(any())).thenReturn(Collections.emptyList());
        when(historyRepository.save(any())).thenReturn(new History());

        CompletableFuture<Post> resultFuture = postService.processPostAsync(postId, requestBody);

        Post result = resultFuture.join();
        assertNotNull(result);
    }

    @Test
    public void testDisablePostAsync() {
        Long postId = 1L;
        Post post = new Post();

        when(postRepository.save(any())).thenReturn(post);
        when(commentRepository.saveAll(any())).thenReturn(Collections.emptyList());
        when(historyRepository.save(any())).thenReturn(new History());

        CompletableFuture<Post> resultFuture = postService.disablePostAsync(postId);

        Post result = resultFuture.join();
        assertNotNull(result);
    }

    @Test
    public void testReprocessPostAsync() {
        Long postId = 1L;
        Post post = new Post();

        when(postRepository.save(any())).thenReturn(post);
        when(commentRepository.saveAll(any())).thenReturn(Collections.emptyList());
        when(historyRepository.save(any())).thenReturn(new History());

        CompletableFuture<Post> resultFuture = postService.reprocessPostAsync(postId);

        Post result = resultFuture.join();
        assertNotNull(result);
    }
}
