package br.senac.academia.repository;

import br.senac.academia.model.Aluno;
import br.senac.academia.model.enums.StatusAluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    Optional<Aluno> findByEmail(String email);
    Optional<Aluno> findByCpf(String cpf);
    List<Aluno> findByStatus(StatusAluno status);
    List<Aluno> findByPlanoId(Long planoId);
    List<Aluno> findByInstrutorId(Long instrutorId);

    @Query("SELECT AVG(YEAR(CURRENT_DATE) - YEAR(a.dataNascimento)) FROM Aluno a")
    Double calcularMediaIdade();

    @Query("SELECT COUNT(a) FROM Aluno a WHERE a.status = 'ATIVO'")
    Long countAlunosAtivos();
}