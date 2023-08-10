package br.com.compass.Desafio3.service;

import br.com.compass.Desafio3.DTO.PostDto;
import br.com.compass.Desafio3.entity.Comment;
import br.com.compass.Desafio3.entity.Post;

import java.util.List;

public interface PostService {

    List<Post> queryPosts();

    Post processPost(Long postId, Post requestBody);

    Post disablePost(Long postId);

    Post reprocessPost(Long postId);

    Post findPostById(Long postId);

    Comment[] findCommentsForPost(Long postId);

    Comment findCommentById(Long commentId);

    PostDto convertToDto(Post post);

    void savePost(Post post);

}
