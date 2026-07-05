package br.senac.academia.service;

import br.senac.academia.model.Treino;
import br.senac.academia.model.ExercicioTreino;
import br.senac.academia.model.ExercicioTreinoId;
import br.senac.academia.repository.TreinoRepository;
import br.senac.academia.repository.ExercicioTreinoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TreinoService {

    @Autowired
    private TreinoRepository treinoRepository;

    @Autowired
    private ExercicioTreinoRepository exercicioTreinoRepository;

    public List<Treino> findAll() {
        return treinoRepository.findAll();
    }

    public Optional<Treino> findById(Long id) {
        return treinoRepository.findById(id);
    }

    public Treino save(Treino treino) {
        return treinoRepository.save(treino);
    }

    public void deleteById(Long id) {
        treinoRepository.deleteById(id);
    }

    public List<Treino> findByAluno(Long alunoId) {
        return treinoRepository.findByAlunoId(alunoId);
    }

    public List<Treino> findByInstrutor(Long instrutorId) {
        return treinoRepository.findByInstrutorId(instrutorId);
    }

    public List<Treino> findByAtivoTrue() {
        return treinoRepository.findByAtivoTrue();
    }

    // Ajustado para usar a consulta JPQL do Repository
    public List<Treino> findTreinosVencidos() {
        return treinoRepository.findTreinosVencidos();
    }

    public Long countTreinosAtivosPorAluno(Long alunoId) {
        return treinoRepository.countTreinosAtivosPorAluno(alunoId);
    }

    @Transactional
    public Treino criarTreinoComExercicios(Treino novoTreino) {

        Treino treinoSalvo = treinoRepository.save(novoTreino);

        if (novoTreino.getExercicios() != null) {
            for (ExercicioTreino et : novoTreino.getExercicios()) {

                ExercicioTreinoId id = new ExercicioTreinoId(
                        treinoSalvo.getId(),
                        et.getExercicio().getId()
                );

                et.setId(id);
                et.setTreino(treinoSalvo);

                exercicioTreinoRepository.save(et);
            }
        }

        return treinoSalvo;
    }

    public List<Treino> buscarTreinosPorNomeExercicio(String nomeExercicio) {
        return treinoRepository.buscarTreinosPorNomeExercicio(nomeExercicio);
    }
}