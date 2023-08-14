package br.com.compass.Desafio3.service;

import br.com.compass.Desafio3.entity.Comment;
import br.com.compass.Desafio3.entity.Post;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PostService {

    CompletableFuture<List<Post>> queryPostsAsync();

    CompletableFuture<Post> processPostAsync(Long postId, Post requestBody);

    CompletableFuture<Post> disablePostAsync(Long postId);

    CompletableFuture<Post> reprocessPostAsync(Long postId);

    Post findPostById(Long postId);

    Comment[] findCommentsForPost(Long postId);

    Comment findCommentById(Long commentId);

    void savePost(Post post);

}
