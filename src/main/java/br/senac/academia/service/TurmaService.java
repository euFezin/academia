package br.senac.academia.service;

import br.senac.academia.model.Turma;
import br.senac.academia.repository.TurmaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TurmaService {

    @Autowired
    private TurmaRepository turmaRepository;

    public List<Turma> findAll() {
        return turmaRepository.findAll();
    }

    public Optional<Turma> findById(Long id) {
        return turmaRepository.findById(id);
    }

    public Turma save(Turma turma) {
        return turmaRepository.save(turma);
    }

    public void deleteById(Long id) {
        turmaRepository.deleteById(id);
    }

    public List<Turma> findByAtivoTrue() {
        return turmaRepository.findByAtivoTrue();
    }

    public List<Turma> findByNivel(String nivel) {
        return turmaRepository.findByNivel(nivel);
    }

    public List<Turma> findTurmasComVagas() {
        return turmaRepository.findTurmasComVagas();
    }
}