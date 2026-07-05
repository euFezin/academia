package br.senac.academia.service;

import br.senac.academia.model.Aula;
import br.senac.academia.repository.AulaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AulaService {

    @Autowired
    private AulaRepository aulaRepository;

    public List<Aula> findAll() {
        return aulaRepository.findAll();
    }

    public Optional<Aula> findById(Long id) {
        return aulaRepository.findById(id);
    }

    public Aula save(Aula aula) {
        return aulaRepository.save(aula);
    }

    public void deleteById(Long id) {
        aulaRepository.deleteById(id);
    }

    public List<Aula> findAulasAtivas() {
        return aulaRepository.findByAtivaTrue();
    }

    public List<Aula> findProximasAulas() {
        return aulaRepository.findProximasAulas();
    }

    public List<Aula> findByInstrutor(Long instrutorId) {
        return aulaRepository.findByInstrutorId(instrutorId);
    }

    public List<Aula> findByTurma(Long turmaId) {
        return aulaRepository.findByTurmaId(turmaId);
    }

    public List<Aula> findByPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return aulaRepository.findByDataHoraBetween(inicio, fim);
    }

    public Long countAulasPorTurma(Long turmaId) {
        return aulaRepository.countAulasAtivasPorTurma(turmaId);
    }
}