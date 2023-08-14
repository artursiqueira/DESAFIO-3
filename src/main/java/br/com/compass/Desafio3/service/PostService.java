package br.com.compass.Desafio3.service;

import br.com.compass.Desafio3.entity.Comment;
import br.com.compass.Desafio3.entity.History;
import br.com.compass.Desafio3.entity.Post;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface PostService {

    CompletableFuture<List<Post>> queryPostsAsync();

    CompletableFuture<Post> processPostAsync(Long postId);

    CompletableFuture<Post> disablePostAsync(Long postId);

    CompletableFuture<Post> reprocessPostAsync(Long postId);

    CompletableFuture<Post> findPostByIdAsync(Long postId);

    CompletableFuture<Comment[]> findCommentsForPostAsync(Long postId);

    CompletableFuture<Comment> findCommentByIdAsync(Long commentId);

    CompletableFuture<Optional<History>> findHistoryForPostAsync(Long postId);

    void savePost(Post post);

}
