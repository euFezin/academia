package br.senac.academia.repository;

import br.senac.academia.model.AvaliacaoFisica;
import br.senac.academia.model.AvaliacaoFisicaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AvaliacaoFisicaRepository extends JpaRepository<AvaliacaoFisica, AvaliacaoFisicaId> {

    List<AvaliacaoFisica> findByAlunoId(Long alunoId);

    List<AvaliacaoFisica> findByInstrutorId(Long instrutorId);

    @Query("SELECT a FROM AvaliacaoFisica a WHERE a.id.dataAvaliacao BETWEEN :inicio AND :fim")
    List<AvaliacaoFisica> findByDataAvaliacaoBetween(LocalDate inicio, LocalDate fim);

    @Query("SELECT a FROM AvaliacaoFisica a WHERE a.aluno.id = :alunoId ORDER BY a.id.dataAvaliacao DESC")
    List<AvaliacaoFisica> findUltimasAvaliacoesByAluno(Long alunoId);

    @Query("SELECT AVG(a.peso) FROM AvaliacaoFisica a WHERE a.aluno.id = :alunoId")
    Double calcularMediaPesoAluno(Long alunoId);
}