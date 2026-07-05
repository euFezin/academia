package br.senac.academia.controller;

import br.senac.academia.model.Turma;
import br.senac.academia.service.TurmaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/turmas")
public class TurmaController {

    @Autowired
    private TurmaService turmaService;

    @GetMapping
    public List<Turma> getAllTurmas() {
        return turmaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Turma> getTurmaById(@PathVariable Long id) {
        Optional<Turma> turma = turmaService.findById(id);
        return turma.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Turma createTurma(@RequestBody Turma turma) {
        return turmaService.save(turma);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Turma> updateTurma(@PathVariable Long id, @RequestBody Turma turmaDetails) {
        Optional<Turma> turmaOpt = turmaService.findById(id);
        if (turmaOpt.isPresent()) {
            Turma turma = turmaOpt.get();
            turma.setNome(turmaDetails.getNome());
            turma.setDescricao(turmaDetails.getDescricao());
            turma.setCapacidadeMaxima(turmaDetails.getCapacidadeMaxima());
            turma.setIdadeMinima(turmaDetails.getIdadeMinima());
            turma.setNivel(turmaDetails.getNivel());
            turma.setAtivo(turmaDetails.getAtivo());
            return ResponseEntity.ok(turmaService.save(turma));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTurma(@PathVariable Long id) {
        if (turmaService.findById(id).isPresent()) {
            turmaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/ativas")
    public List<Turma> getTurmasAtivas() {
        return turmaService.findByAtivoTrue();
    }

    @GetMapping("/nivel/{nivel}")
    public List<Turma> getTurmasByNivel(@PathVariable String nivel) {
        return turmaService.findByNivel(nivel);
    }

    @GetMapping("/com-vagas")
    public List<Turma> getTurmasComVagas() {
        return turmaService.findTurmasComVagas();
    }
}