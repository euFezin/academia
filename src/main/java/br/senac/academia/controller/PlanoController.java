package br.senac.academia.controller;

import br.senac.academia.model.Plano;
import br.senac.academia.service.PlanoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/planos")
public class PlanoController {

    @Autowired
    private PlanoService planoService;

    @GetMapping
    public List<Plano> getAllPlanos() {
        return planoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Plano> getPlanoById(@PathVariable Long id) {
        Optional<Plano> plano = planoService.findById(id);
        return plano.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Plano createPlano(@RequestBody Plano plano) {
        return planoService.save(plano);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Plano> updatePlano(@PathVariable Long id, @RequestBody Plano planoDetails) {
        Optional<Plano> planoOpt = planoService.findById(id);
        if (planoOpt.isPresent()) {
            Plano plano = planoOpt.get();
            plano.setNome(planoDetails.getNome());
            plano.setDescricao(planoDetails.getDescricao());
            plano.setValorMensal(planoDetails.getValorMensal());
            plano.setDuracaoMeses(planoDetails.getDuracaoMeses());
            plano.setAulasPorSemana(planoDetails.getAulasPorSemana());
            plano.setTipo(planoDetails.getTipo());
            plano.setAtivo(planoDetails.getAtivo());
            return ResponseEntity.ok(planoService.save(plano));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlano(@PathVariable Long id) {
        if (planoService.findById(id).isPresent()) {
            planoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/ativos")
    public List<Plano> getPlanosAtivos() {
        return planoService.findPlanosAtivos();
    }

    @GetMapping("/faixa-preco")
    public List<Plano> getPlanosPorFaixaPreco(@RequestParam Double min, @RequestParam Double max) {
        return planoService.findByFaixaPreco(min, max);
    }

    @GetMapping("/{id}/total-alunos")
    public ResponseEntity<Long> getTotalAlunosNoPlano(@PathVariable Long id) {
        Long totalAlunos = planoService.countAlunosNoPlano(id);
        return ResponseEntity.ok(totalAlunos);
    }
}