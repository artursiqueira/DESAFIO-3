package br.com.compass.Desafio3.DTO;

import br.com.compass.Desafio3.enums.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistoryDto {
    private Long id;
    private Date date;
    private PostStatus status;

}
