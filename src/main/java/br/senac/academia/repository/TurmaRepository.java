package br.senac.academia.repository;

import br.senac.academia.model.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long> {
    List<Turma> findByAtivoTrue();
    List<Turma> findByNivel(String nivel);

    @Query("SELECT t FROM Turma t WHERE t.capacidadeMaxima > (SELECT COUNT(a) FROM t.aulas a WHERE a.ativa = true)")
    List<Turma> findTurmasComVagas();
}