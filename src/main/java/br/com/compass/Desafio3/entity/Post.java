package br.com.compass.Desafio3.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Post {
    private Long id;
    private String title;
    private String body;
    private List<Comment> comments;
    private List<History> history;

}
