package br.com.compass.Desafio3.repository;

import br.com.compass.Desafio3.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    Optional<History> findByPostId(Long postId);
}
