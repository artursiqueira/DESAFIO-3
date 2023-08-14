package br.com.compass.Desafio3.service.impl;

import br.com.compass.Desafio3.DTO.PostDto;
import br.com.compass.Desafio3.entity.Comment;
import br.com.compass.Desafio3.entity.History;
import br.com.compass.Desafio3.entity.Post;
import br.com.compass.Desafio3.enums.PostStatus;
import br.com.compass.Desafio3.repository.CommentRepository;
import br.com.compass.Desafio3.repository.HistoryRepository;
import br.com.compass.Desafio3.repository.PostRepository;
import br.com.compass.Desafio3.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class PostServiceImpl implements PostService {

    private static final String EXTERNAL_API_URL = "https://jsonplaceholder.typicode.com";
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final HistoryRepository historyRepository;
    private final ModelMapper modelMapper;

    public PostServiceImpl(ModelMapper modelMapper, PostRepository postRepository, CommentRepository commentRepository, HistoryRepository historyRepository) {
        this.modelMapper = modelMapper;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.historyRepository = historyRepository;
    }

    @Async
    @Override
    public CompletableFuture<List<Post>> queryPostsAsync() {
        List<Post> posts = new ArrayList<>();

        RestTemplate restTemplate = new RestTemplate();
        Post[] postsArray = restTemplate.getForObject(EXTERNAL_API_URL + "/posts", Post[].class);

        if (postsArray != null) {
            for (Post post : postsArray) {
                post.setHistory(new ArrayList<>());

                History history = new History();
                history.setId(post.getId());
                history.setDate(new Date());
                history.setStatus(PostStatus.ENABLED);
                history.setPost(post);
                post.getHistory().add(history);

                List<Comment> comments = List.of(findCommentsForPost(post.getId()));
                for (Comment comment : comments) {
                    comment.setPost(post);
                }
                post.setComments(comments);

                posts.add(post);
            }
        }

        postRepository.saveAll(posts);

        return CompletableFuture.completedFuture(posts);
    }

    @Async
    @Override
    public CompletableFuture<Post> processPostAsync(Long postId, Post requestBody) {
        Post newPost = new Post();
        newPost.setId(postId);
        newPost.setBody(requestBody.getBody());
        newPost.setTitle(requestBody.getTitle());

        History enabledHistory = new History();
        enabledHistory.setId(postId);
        enabledHistory.setDate(new Date());
        enabledHistory.setStatus(PostStatus.ENABLED);
        enabledHistory.setPost(newPost);
        newPost.getHistory().add(enabledHistory);

        List<Comment> comments = List.of(findCommentsForPost(postId));
        for (Comment comment : comments) {
            comment.setPost(newPost);
        }
        newPost.setComments(comments);

        savePost(newPost);
        historyRepository.save(enabledHistory);
        commentRepository.saveAll(comments);

        return CompletableFuture.completedFuture(newPost);
    }

    @Async
    @Override
    public CompletableFuture<Post> disablePostAsync(Long postId) {
        Post post = findPostById(postId);

        List<Comment> comments = List.of(findCommentsForPost(post.getId()));
        for (Comment comment : comments) {
            comment.setPost(post);
        }
        post.setComments(comments);

        History history = new History();
        history.setId(postId);
        history.setDate(new Date());
        history.setStatus(PostStatus.DISABLED);
        history.setPost(post);

        post.getHistory().add(history);
        post.setHistory(Collections.singletonList(history));

        postRepository.save(post);
        historyRepository.save(history);

        return CompletableFuture.completedFuture(post);
    }

    @Async
    @Override
    public CompletableFuture<Post> reprocessPostAsync(Long postId) {
        Post post = findPostById(postId);

        if (post != null) {
            post.setId(postId);
            post.setBody(post.getBody());
            post.setTitle(post.getTitle());

            List<Comment> comments = List.of(findCommentsForPost(post.getId()));
            for (Comment comment : comments) {
                comment.setPost(post);
            }
            post.setComments(comments);

            History enabledHistory = new History();
            enabledHistory.setId(postId);
            enabledHistory.setDate(new Date());
            enabledHistory.setStatus(PostStatus.ENABLED);
            enabledHistory.setPost(post);
            post.getHistory().add(enabledHistory);

            savePost(post);
        }

        return CompletableFuture.completedFuture(post);
    }





    @Override
    public Post findPostById(Long postId) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(EXTERNAL_API_URL + "/posts/" + postId, Post.class);
    }

    @Override
    public Comment[] findCommentsForPost(Long postId) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(EXTERNAL_API_URL + "/posts/" + postId + "/comments", Comment[].class);
    }

    @Override
    public Comment findCommentById(Long commentId) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(EXTERNAL_API_URL + "/comments/" + commentId, Comment.class);
    }

    @Override
    public void savePost(Post post) {
        postRepository.save(post);
    }

    @Override
    public PostDto convertToDto(Post post) {
        return modelMapper.map(post, PostDto.class);
    }
}
