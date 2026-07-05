package br.senac.academia.repository;

import br.senac.academia.model.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AulaRepository extends JpaRepository<Aula, Long> {
    List<Aula> findByAtivaTrue();
    List<Aula> findByInstrutorId(Long instrutorId);
    List<Aula> findByTurmaId(Long turmaId);
    List<Aula> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT a FROM Aula a WHERE a.dataHora > CURRENT_TIMESTAMP AND a.ativa = true")
    List<Aula> findProximasAulas();

    @Query("SELECT COUNT(a) FROM Aula a WHERE a.turma.id = :turmaId AND a.ativa = true")
    Long countAulasAtivasPorTurma(Long turmaId);
}