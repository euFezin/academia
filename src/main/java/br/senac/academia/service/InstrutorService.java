package br.senac.academia.service;

import br.senac.academia.model.Instrutor;
import br.senac.academia.model.enums.StatusInstrutor;
import br.senac.academia.repository.InstrutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstrutorService {

    @Autowired
    private InstrutorRepository instrutorRepository;

    public List<Instrutor> findAll() {
        return instrutorRepository.findAll();
    }

    public Optional<Instrutor> findById(Long id) {
        return instrutorRepository.findById(id);
    }

    public Instrutor save(Instrutor instrutor) {
        return instrutorRepository.save(instrutor);
    }

    public void deleteById(Long id) {
        instrutorRepository.deleteById(id);
    }

    public List<Instrutor> findByStatus(StatusInstrutor status) {
        return instrutorRepository.findByStatus(status);
    }

    public List<Instrutor> findByEspecialidade(String especialidade) {
        return instrutorRepository.findByEspecialidadeContainingIgnoreCase(especialidade);
    }

    public List<Instrutor> findInstrutoresComMaisAlunos(int minAlunos) {
        return instrutorRepository.findInstrutoresComMaisDeNAlunos(minAlunos);
    }

    public Optional<Instrutor> findByEmail(String email) {
        return instrutorRepository.findByEmail(email);
    }
}