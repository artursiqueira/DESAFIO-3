package br.com.compass.Desafio3.service.impl;

import br.com.compass.Desafio3.entity.Comment;
import br.com.compass.Desafio3.entity.History;
import br.com.compass.Desafio3.entity.Post;
import br.com.compass.Desafio3.enums.PostStatus;
import br.com.compass.Desafio3.repository.CommentRepository;
import br.com.compass.Desafio3.repository.HistoryRepository;
import br.com.compass.Desafio3.repository.PostRepository;
import br.com.compass.Desafio3.service.PostService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    private static final String EXTERNAL_API_URL = "https://jsonplaceholder.typicode.com";
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final HistoryRepository historyRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, CommentRepository commentRepository, HistoryRepository historyRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.historyRepository = historyRepository;
    }

    @Async
    @Override
    public CompletableFuture<List<Post>> queryPostsAsync() {
        List<Post> posts = postRepository.findAll();

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Post post : posts) {
            Long postId = post.getId();

            CompletableFuture<Comment[]> commentsFuture = findCommentsForPostAsync(postId);
            CompletableFuture<Optional<History>> historyFuture = findHistoryForPostAsync(postId);

            CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(commentsFuture, historyFuture)
                    .thenApplyAsync(voidResult -> {
                        try {
                            Comment[] comments = commentsFuture.get();
                            Optional<History> historyOptional = historyFuture.get();

                            if (comments != null) {
                                List<Comment> commentsList = Arrays.asList(comments);
                                post.setComments(commentsList);
                            }

                            historyOptional.ifPresent(history -> {
                                List<History> historyList = new ArrayList<>();
                                historyList.add(history);
                                post.setHistory(historyList);
                            });
                        } catch (InterruptedException | ExecutionException e) {
                        }

                        return null;
                    });

            futures.add(combinedFuture);
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        return allOf.thenApplyAsync(voidResult -> posts);
    }

    @Async
    @Override
    public CompletableFuture<Post> processPostAsync(Long postId) {
        RestTemplate restTemplate = new RestTemplate();
        Post externalPost = restTemplate.getForObject(EXTERNAL_API_URL + "/posts/" + postId, Post.class);

        if (externalPost != null) {
            History history = new History();
            history.setId(postId);
            history.setDate(new Date());
            history.setStatus(PostStatus.ENABLED);
            history.setPost(externalPost);

            externalPost.setHistory(Collections.singletonList(history));

            CompletableFuture<Comment[]> commentsFuture = findCommentsForPostAsync(postId);

            try {
                Comment[] commentsArray = commentsFuture.get();
                if (commentsArray != null) {
                    List<Comment> comments = Arrays.asList(commentsArray);

                    for (Comment comment : comments) {
                        comment.setPost(externalPost);
                    }

                    externalPost.setComments(comments);
                }
            } catch (InterruptedException | ExecutionException e) {
                history.setStatus(PostStatus.FAILED);
            }

            savePost(externalPost);

            return CompletableFuture.completedFuture(externalPost);
        }

        return CompletableFuture.completedFuture(null);
    }

    @Async
    @Override
    public CompletableFuture<Post> disablePostAsync(Long postId) {
        return findPostByIdAsync(postId)
                .thenCompose(post -> {
                    if (post != null) {
                        return findCommentsForPostAsync(postId)
                                .thenApply(commentsArray -> {
                                    List<Comment> comments = Arrays.asList(commentsArray);

                                    for (Comment comment : comments) {
                                        comment.setPost(post);
                                    }

                                    History history = new History();
                                    history.setId(postId);
                                    history.setDate(new Date());
                                    history.setStatus(PostStatus.DISABLED);
                                    history.setPost(post);

                                    post.getHistory().add(history);
                                    post.setHistory(Collections.singletonList(history));
                                    post.setComments(comments);

                                    for (Comment comment : comments) {
                                        commentRepository.save(comment);
                                    }

                                    savePost(post);
                                    historyRepository.save(history);

                                    return post;
                                });
                    } else {
                        return CompletableFuture.completedFuture(null);
                    }
                });
    }

    @Async
    @Override
    public CompletableFuture<Post> reprocessPostAsync(Long postId) {
        RestTemplate restTemplate = new RestTemplate();
        Post externalPost = restTemplate.getForObject(EXTERNAL_API_URL + "/posts/" + postId, Post.class);

        if (externalPost != null) {
            History history = new History();
            history.setId(postId);
            history.setDate(new Date());
            history.setStatus(PostStatus.ENABLED);
            history.setPost(externalPost);

            externalPost.setHistory(Collections.singletonList(history));

            CompletableFuture<Comment[]> commentsFuture = findCommentsForPostAsync(postId);

            try {
                Comment[] commentsArray = commentsFuture.get();
                if (commentsArray != null) {
                    List<Comment> newComments = Arrays.stream(commentsArray)
                            .map(commentDto -> {
                                Comment comment = new Comment();
                                comment.setId(commentDto.getId());
                                comment.setBody(commentDto.getBody());
                                return comment;
                            })
                            .collect(Collectors.toList());

                    externalPost.setComments(newComments);
                }
            } catch (InterruptedException | ExecutionException e) {
                history.setStatus(PostStatus.FAILED);
            }

            savePost(externalPost);

            return CompletableFuture.completedFuture(externalPost);
        }

        return CompletableFuture.completedFuture(null);
    }





    @Async
    @Override
    public CompletableFuture<Post> findPostByIdAsync(Long postId) {
        RestTemplate restTemplate = new RestTemplate();
        Post post = restTemplate.getForObject(EXTERNAL_API_URL + "/posts/" + postId, Post.class);
        return CompletableFuture.completedFuture(post);
    }

    @Async
    @Override
    public CompletableFuture<Comment[]> findCommentsForPostAsync(Long postId) {
        RestTemplate restTemplate = new RestTemplate();
        Comment[] comments = restTemplate.getForObject(EXTERNAL_API_URL + "/posts/" + postId + "/comments", Comment[].class);
        return CompletableFuture.completedFuture(comments);
    }
    @Async
    @Override
    public CompletableFuture<Comment> findCommentByIdAsync(Long commentId) {
        RestTemplate restTemplate = new RestTemplate();
        Comment comment = restTemplate.getForObject(EXTERNAL_API_URL + "/comments/" + commentId, Comment.class);
        return CompletableFuture.completedFuture(comment);
    }

    @Async
    @Override
    public CompletableFuture<Optional<History>> findHistoryForPostAsync(Long postId) {
        Optional<History> historyOptional = historyRepository.findByPostId(postId);
        return CompletableFuture.completedFuture(historyOptional);
    }

    @Override
    public void savePost(Post post) {
        postRepository.save(post);
    }
}
