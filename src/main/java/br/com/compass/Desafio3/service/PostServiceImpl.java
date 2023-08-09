package br.com.compass.Desafio3.service;

import br.com.compass.Desafio3.entity.Comment;
import br.com.compass.Desafio3.entity.Post;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PostServiceImpl {

    private static final String EXTERNAL_API_URL = "https://jsonplaceholder.typicode.com";

    public List<Post> fetchPosts() {
        List<Post> posts = new ArrayList<>();

        RestTemplate restTemplate = new RestTemplate();
        Post[] postsArray = restTemplate.getForObject(EXTERNAL_API_URL + "/posts", Post[].class);

        if (postsArray != null) {
            for (Post post : postsArray) {
                post.setHistory(new ArrayList<>()); // Initialize history

                Comment[] comments = fetchCommentsForPost(post.getId());
                post.setComments(Arrays.asList(comments));

                posts.add(post);
            }
        }

        return posts;
    }

    public Post fetchPostById(Long postId) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(EXTERNAL_API_URL + "/posts/" + postId, Post.class);
    }

    public Comment[] fetchCommentsForPost(Long postId) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(EXTERNAL_API_URL + "/posts/" + postId + "/comments", Comment[].class);
    }

    public Comment fetchCommentById(Long commentId) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(EXTERNAL_API_URL + "/comments/" + commentId, Comment.class);
    }
}
