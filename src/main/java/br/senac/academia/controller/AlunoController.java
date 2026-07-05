package br.senac.academia.controller;

import br.senac.academia.model.Aluno;
import br.senac.academia.model.enums.StatusAluno;
import br.senac.academia.service.AlunoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    @Autowired
    private AlunoService alunoService;

    @GetMapping
    public List<Aluno> getAllAlunos() {
        return alunoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aluno> getAlunoById(@PathVariable Long id) {
        Optional<Aluno> aluno = alunoService.findById(id);
        return aluno.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Aluno createAluno(@RequestBody Aluno aluno) {
        return alunoService.save(aluno);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Aluno> updateAluno(@PathVariable Long id, @RequestBody Aluno alunoDetails) {
        Optional<Aluno> alunoOpt = alunoService.findById(id);
        if (alunoOpt.isPresent()) {
            Aluno aluno = alunoOpt.get();
            aluno.setNome(alunoDetails.getNome());
            aluno.setEmail(alunoDetails.getEmail());
            aluno.setTelefone(alunoDetails.getTelefone());
            aluno.setEndereco(alunoDetails.getEndereco());
            aluno.setStatus(alunoDetails.getStatus());
            return ResponseEntity.ok(alunoService.save(aluno));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAluno(@PathVariable Long id) {
        if (alunoService.findById(id).isPresent()) {
            alunoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/status/{status}")
    public List<Aluno> getAlunosByStatus(@PathVariable StatusAluno status) {
        return alunoService.findByStatus(status);
    }

    @GetMapping("/plano/{planoId}")
    public List<Aluno> getAlunosByPlano(@PathVariable Long planoId) {
        return alunoService.findByPlano(planoId);
    }

    @GetMapping("/instrutor/{instrutorId}")
    public List<Aluno> getAlunosByInstrutor(@PathVariable Long instrutorId) {
        return alunoService.findByInstrutor(instrutorId);
    }

    @GetMapping("/estatisticas/media-idade")
    public ResponseEntity<Double> getMediaIdadeAlunos() {
        Double mediaIdade = alunoService.calcularMediaIdadeAlunos();
        return ResponseEntity.ok(mediaIdade);
    }

    @GetMapping("/estatisticas/total-ativos")
    public ResponseEntity<Long> getTotalAlunosAtivos() {
        Long totalAtivos = alunoService.countAlunosAtivos();
        return ResponseEntity.ok(totalAtivos);
    }
}