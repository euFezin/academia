package br.senac.academia.controller;

import br.senac.academia.model.Aula;
import br.senac.academia.service.AulaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/aulas")
public class AulaController {

    @Autowired
    private AulaService aulaService;

    @GetMapping
    public List<Aula> getAllAulas() {
        return aulaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aula> getAulaById(@PathVariable Long id) {
        Optional<Aula> aula = aulaService.findById(id);
        return aula.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Aula createAula(@RequestBody Aula aula) {
        return aulaService.save(aula);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Aula> updateAula(@PathVariable Long id, @RequestBody Aula aulaDetails) {
        Optional<Aula> aulaOpt = aulaService.findById(id);
        if (aulaOpt.isPresent()) {
            Aula aula = aulaOpt.get();
            aula.setDataHora(aulaDetails.getDataHora());
            aula.setDuracaoMinutos(aulaDetails.getDuracaoMinutos());
            aula.setObservacoes(aulaDetails.getObservacoes());
            aula.setAtiva(aulaDetails.getAtiva());
            aula.setTurma(aulaDetails.getTurma());
            aula.setInstrutor(aulaDetails.getInstrutor());
            return ResponseEntity.ok(aulaService.save(aula));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAula(@PathVariable Long id) {
        if (aulaService.findById(id).isPresent()) {
            aulaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/ativas")
    public List<Aula> getAulasAtivas() {
        return aulaService.findAulasAtivas();
    }

    @GetMapping("/proximas")
    public List<Aula> getProximasAulas() {
        return aulaService.findProximasAulas();
    }

    @GetMapping("/instrutor/{instrutorId}")
    public List<Aula> getAulasByInstrutor(@PathVariable Long instrutorId) {
        return aulaService.findByInstrutor(instrutorId);
    }

    @GetMapping("/turma/{turmaId}")
    public List<Aula> getAulasByTurma(@PathVariable Long turmaId) {
        return aulaService.findByTurma(turmaId);
    }

    @GetMapping("/periodo")
    public List<Aula> getAulasByPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return aulaService.findByPeriodo(inicio, fim);
    }

    @GetMapping("/turma/{turmaId}/total")
    public ResponseEntity<Long> getTotalAulasPorTurma(@PathVariable Long turmaId) {
        Long total = aulaService.countAulasPorTurma(turmaId);
        return ResponseEntity.ok(total);
    }
}