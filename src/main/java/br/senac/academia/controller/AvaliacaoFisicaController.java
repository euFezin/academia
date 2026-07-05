package br.senac.academia.controller;

import br.senac.academia.model.AvaliacaoFisica;
import br.senac.academia.model.AvaliacaoFisicaId;
import br.senac.academia.service.AvaliacaoFisicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/avaliacoes-fisicas")
public class AvaliacaoFisicaController {

    @Autowired
    private AvaliacaoFisicaService avaliacaoFisicaService;

    @PostMapping
    public AvaliacaoFisica createAvaliacao(@RequestBody AvaliacaoFisica avaliacao) {
        return avaliacaoFisicaService.save(avaliacao);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAvaliacao(@RequestParam Long alunoId,
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataAvaliacao) {
        AvaliacaoFisicaId id = new AvaliacaoFisicaId(alunoId, dataAvaliacao);
        if (avaliacaoFisicaService.findById(id).isPresent()) {
            avaliacaoFisicaService.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/aluno/{alunoId}")
    public List<AvaliacaoFisica> getAvaliacoesByAluno(@PathVariable Long alunoId) {
        return avaliacaoFisicaService.findByAluno(alunoId);
    }

    @GetMapping("/instrutor/{instrutorId}")
    public List<AvaliacaoFisica> getAvaliacoesByInstrutor(@PathVariable Long instrutorId) {
        return avaliacaoFisicaService.findByInstrutor(instrutorId);
    }

    @GetMapping("/aluno/{alunoId}/ultimas")
    public List<AvaliacaoFisica> getUltimasAvaliacoesByAluno(@PathVariable Long alunoId) {
        return avaliacaoFisicaService.findUltimasAvaliacoesByAluno(alunoId);
    }

    @GetMapping("/calcular-imc")
    public ResponseEntity<Double> calcularIMC(@RequestParam BigDecimal peso, @RequestParam BigDecimal altura) {
        Double imc = avaliacaoFisicaService.calcularIMC(peso, altura);
        return ResponseEntity.ok(imc);
    }

    @GetMapping("/aluno/{alunoId}/media-peso")
    public ResponseEntity<Double> getMediaPesoAluno(@PathVariable Long alunoId) {
        Double mediaPeso = avaliacaoFisicaService.calcularMediaPesoAluno(alunoId);
        return ResponseEntity.ok(mediaPeso);
    }

    @GetMapping("/periodo")
    public List<AvaliacaoFisica> getAvaliacoesByPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return avaliacaoFisicaService.findByPeriodo(inicio, fim);
    }
}