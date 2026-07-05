package br.senac.academia.repository;

import br.senac.academia.model.Treino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TreinoRepository extends JpaRepository<Treino, Long> {
    List<Treino> findByAlunoId(Long alunoId);
    List<Treino> findByInstrutorId(Long instrutorId);
    List<Treino> findByAtivoTrue();

    @Query("SELECT t FROM Treino t WHERE t.ativo = true AND t.dataValidade < CURRENT_DATE")
    List<Treino> findTreinosVencidos();

    @Query("SELECT COUNT(t) FROM Treino t WHERE t.aluno.id = :alunoId AND t.ativo = true")
    Long countTreinosAtivosPorAluno(Long alunoId);

    @Query("SELECT t FROM Treino t JOIN t.exercicios et JOIN et.exercicio e WHERE e.nome LIKE %:nomeExercicio%")
    List<Treino> buscarTreinosPorNomeExercicio(String nomeExercicio);
}