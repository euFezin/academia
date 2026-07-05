package br.senac.academia.service;

import br.senac.academia.model.Aluno;
import br.senac.academia.model.enums.StatusAluno;
import br.senac.academia.repository.AlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlunoService {

    @Autowired
    private AlunoRepository alunoRepository;

    public List<Aluno> findAll() {
        return alunoRepository.findAll();
    }

    public Optional<Aluno> findById(Long id) {
        return alunoRepository.findById(id);
    }

    public Aluno save(Aluno aluno) {
        Optional<Aluno> alunoComCpf = alunoRepository.findByCpf(aluno.getCpf());
        if (alunoComCpf.isPresent() && !alunoComCpf.get().getId().equals(aluno.getId())) {
            throw new RuntimeException("Já existe um aluno cadastrado com este CPF: " + aluno.getCpf());
        }

        Optional<Aluno> alunoComEmail = alunoRepository.findByEmail(aluno.getEmail());
        if (alunoComEmail.isPresent() && !alunoComEmail.get().getId().equals(aluno.getId())) {
            throw new RuntimeException("Já existe um aluno cadastrado com este Email: " + aluno.getEmail());
        }

        return alunoRepository.save(aluno);
    }

    public void deleteById(Long id) {
        alunoRepository.deleteById(id);
    }

    public List<Aluno> findByStatus(StatusAluno status) {
        return alunoRepository.findByStatus(status);
    }

    public List<Aluno> findByPlano(Long planoId) {
        return alunoRepository.findByPlanoId(planoId);
    }

    public List<Aluno> findByInstrutor(Long instrutorId) {
        return alunoRepository.findByInstrutorId(instrutorId);
    }

    public Double calcularMediaIdadeAlunos() {
        return alunoRepository.calcularMediaIdade();
    }

    public Long countAlunosAtivos() {
        return alunoRepository.countAlunosAtivos();
    }

    public Optional<Aluno> findByEmail(String email) {
        return alunoRepository.findByEmail(email);
    }

    public Optional<Aluno> findByCpf(String cpf) {
        return alunoRepository.findByCpf(cpf);
    }
}