package br.senac.academia.repository;

import br.senac.academia.model.Instrutor;
import br.senac.academia.model.enums.StatusInstrutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstrutorRepository extends JpaRepository<Instrutor, Long> {
    Optional<Instrutor> findByEmail(String email);
    Optional<Instrutor> findByCpf(String cpf);
    List<Instrutor> findByStatus(StatusInstrutor status);
    List<Instrutor> findByEspecialidadeContainingIgnoreCase(String especialidade);

    @Query("SELECT i FROM Instrutor i WHERE SIZE(i.alunosOrientados) > :minAlunos")
    List<Instrutor> findInstrutoresComMaisDeNAlunos(int minAlunos);
}