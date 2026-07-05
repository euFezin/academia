package br.senac.academia.repository;

import br.senac.academia.model.ExercicioTreino;
import br.senac.academia.model.ExercicioTreinoId; // Importe a nova classe ID
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExercicioTreinoRepository extends JpaRepository<ExercicioTreino, ExercicioTreinoId> {
}