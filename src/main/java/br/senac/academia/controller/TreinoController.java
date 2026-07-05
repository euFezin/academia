package br.senac.academia.controller;

import br.senac.academia.model.Treino;
import br.senac.academia.service.TreinoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/treinos")
public class TreinoController {

    @Autowired
    private TreinoService treinoService;


    @PostMapping("/com-exercicios")
    public ResponseEntity<Treino> criarTreinoComExercicios(@RequestBody Treino treino) {
        Treino novoTreino = treinoService.criarTreinoComExercicios(treino);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoTreino);
    }

    @PostMapping
    public Treino createTreino(@RequestBody Treino treino) {
        return treinoService.save(treino);
    }

    @GetMapping("/buscar-por-exercicio")
    public ResponseEntity<List<Treino>> buscarTreinosPorNomeExercicio(@RequestParam("nome") String nomeExercicio) {
        List<Treino> treinos = treinoService.buscarTreinosPorNomeExercicio(nomeExercicio);
        if (treinos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(treinos);
    }

    @GetMapping
    public List<Treino> getAllTreinos() {
        return treinoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Treino> getTreinoById(@PathVariable Long id) {
        Optional<Treino> treino = treinoService.findById(id);
        return treino.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Treino> updateTreino(@PathVariable Long id, @RequestBody Treino treinoDetails) {

        Optional<Treino> treinoOpt = treinoService.findById(id);
        if (treinoOpt.isPresent()) {
            Treino treino = treinoOpt.get();
            treino.setNome(treinoDetails.getNome());
            treino.setDescricao(treinoDetails.getDescricao());
            treino.setTipo(treinoDetails.getTipo());
            treino.setDataValidade(treinoDetails.getDataValidade());
            treino.setAtivo(treinoDetails.getAtivo());
            return ResponseEntity.ok(treinoService.save(treino));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTreino(@PathVariable Long id) {
        if (treinoService.findById(id).isPresent()) {
            treinoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/aluno/{alunoId}")
    public List<Treino> getTreinosByAluno(@PathVariable Long alunoId) {
        return treinoService.findByAluno(alunoId);
    }

    @GetMapping("/instrutor/{instrutorId}")
    public List<Treino> getTreinosByInstrutor(@PathVariable Long instrutorId) {
        return treinoService.findByInstrutor(instrutorId);
    }

    @GetMapping("/ativos")
    public List<Treino> getTreinosAtivos() {
        return treinoService.findByAtivoTrue();
    }

    @GetMapping("/vencidos")
    public List<Treino> getTreinosVencidos() {
        return treinoService.findTreinosVencidos();
    }

    @GetMapping("/aluno/{alunoId}/total-ativos")
    public ResponseEntity<Long> getTotalTreinosAtivosPorAluno(@PathVariable Long alunoId) {
        Long total = treinoService.countTreinosAtivosPorAluno(alunoId);
        return ResponseEntity.ok(total);
    }
}