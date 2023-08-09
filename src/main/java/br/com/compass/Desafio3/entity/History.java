package br.com.compass.Desafio3.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class History {
    private Long id;
    private Date date;
    private String status;

}
