package br.senac.academia.repository;

import br.senac.academia.model.Plano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanoRepository extends JpaRepository<Plano, Long> {
    Optional<Plano> findByNome(String nome);
    List<Plano> findByAtivoTrue();

    @Query("SELECT p FROM Plano p WHERE p.valorMensal BETWEEN :valorMin AND :valorMax")
    List<Plano> findByValorMensalBetween(Double valorMin, Double valorMax);

    @Query("SELECT COUNT(a) FROM Aluno a WHERE a.plano.id = :planoId")
    Long countAlunosByPlano(Long planoId);
}