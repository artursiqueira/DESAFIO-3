package br.com.compass.Desafio3.controller;

import br.com.compass.Desafio3.entity.Comment;
import br.com.compass.Desafio3.entity.Post;
import br.com.compass.Desafio3.service.PostServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/posts")
public class PostController {

    @GetMapping
    public ResponseEntity<List<Post>> queryPosts() {
        PostServiceImpl postServiceImpl = new PostServiceImpl();
        List<Post> posts = postServiceImpl.fetchPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable Long postId) {
        PostServiceImpl postServiceImpl = new PostServiceImpl();
        Post post = postServiceImpl.fetchPostById(postId);

        System.out.println("Fetched Post:");
        System.out.println("ID: " + post.getId());
        System.out.println("Title: " + post.getTitle());
        System.out.println("Body: " + post.getBody());

        return ResponseEntity.ok(post);
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<Comment[]> getCommentsForPost(@PathVariable Long postId) {
        PostServiceImpl postServiceImpl = new PostServiceImpl();
        Comment[] comments = postServiceImpl.fetchCommentsForPost(postId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long commentId) {
        PostServiceImpl postServiceImpl = new PostServiceImpl();
        Comment comment = postServiceImpl.fetchCommentById(commentId);
        return ResponseEntity.ok(comment);
    }
}

