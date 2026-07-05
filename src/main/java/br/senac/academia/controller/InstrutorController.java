package br.senac.academia.controller;

import br.senac.academia.model.Instrutor;
import br.senac.academia.model.enums.StatusInstrutor;
import br.senac.academia.service.InstrutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/instrutores")
public class InstrutorController {

    @Autowired
    private InstrutorService instrutorService;

    @GetMapping
    public List<Instrutor> getAllInstrutores() {
        return instrutorService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Instrutor> getInstrutorById(@PathVariable Long id) {
        Optional<Instrutor> instrutor = instrutorService.findById(id);
        return instrutor.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Instrutor createInstrutor(@RequestBody Instrutor instrutor) {
        return instrutorService.save(instrutor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Instrutor> updateInstrutor(@PathVariable Long id, @RequestBody Instrutor instrutorDetails) {
        Optional<Instrutor> instrutorOpt = instrutorService.findById(id);
        if (instrutorOpt.isPresent()) {
            Instrutor instrutor = instrutorOpt.get();
            instrutor.setNome(instrutorDetails.getNome());
            instrutor.setEmail(instrutorDetails.getEmail());
            instrutor.setTelefone(instrutorDetails.getTelefone());
            instrutor.setEspecialidade(instrutorDetails.getEspecialidade());
            instrutor.setSalario(instrutorDetails.getSalario());
            instrutor.setStatus(instrutorDetails.getStatus());
            return ResponseEntity.ok(instrutorService.save(instrutor));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstrutor(@PathVariable Long id) {
        if (instrutorService.findById(id).isPresent()) {
            instrutorService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/status/{status}")
    public List<Instrutor> getInstrutoresByStatus(@PathVariable StatusInstrutor status) {
        return instrutorService.findByStatus(status);
    }

    @GetMapping("/especialidade/{especialidade}")
    public List<Instrutor> getInstrutoresByEspecialidade(@PathVariable String especialidade) {
        return instrutorService.findByEspecialidade(especialidade);
    }

    @GetMapping("/com-mais-alunos")
    public List<Instrutor> getInstrutoresComMaisAlunos(@RequestParam(defaultValue = "5") int minAlunos) {
        return instrutorService.findInstrutoresComMaisAlunos(minAlunos);
    }
}