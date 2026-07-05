package br.senac.academia.service;

import br.senac.academia.model.AvaliacaoFisica;
import br.senac.academia.model.AvaliacaoFisicaId;
import br.senac.academia.repository.AvaliacaoFisicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AvaliacaoFisicaService {

    @Autowired
    private AvaliacaoFisicaRepository avaliacaoFisicaRepository;

    public AvaliacaoFisica save(AvaliacaoFisica avaliacao) {
        return avaliacaoFisicaRepository.save(avaliacao);
    }

    public void delete(AvaliacaoFisicaId id) {
        avaliacaoFisicaRepository.deleteById(id);
    }

    public Optional<AvaliacaoFisica> findById(AvaliacaoFisicaId id) {
        return avaliacaoFisicaRepository.findById(id);
    }

    public List<AvaliacaoFisica> findByAluno(Long alunoId) {
        return avaliacaoFisicaRepository.findByAlunoId(alunoId);
    }

    public List<AvaliacaoFisica> findByInstrutor(Long instrutorId) {
        return avaliacaoFisicaRepository.findByInstrutorId(instrutorId);
    }

    public List<AvaliacaoFisica> findUltimasAvaliacoesByAluno(Long alunoId) {
        return avaliacaoFisicaRepository.findUltimasAvaliacoesByAluno(alunoId);
    }

    public Double calcularIMC(BigDecimal peso, BigDecimal altura) {
        if (altura.compareTo(BigDecimal.ZERO) == 0) return 0.0;
        BigDecimal alturaMetros = altura.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal imc = peso.divide(alturaMetros.pow(2), 2, RoundingMode.HALF_UP);
        return imc.doubleValue();
    }

    public Double calcularMediaPesoAluno(Long alunoId) {
        return avaliacaoFisicaRepository.calcularMediaPesoAluno(alunoId);
    }

    public List<AvaliacaoFisica> findByPeriodo(LocalDate inicio, LocalDate fim) {
        return avaliacaoFisicaRepository.findByDataAvaliacaoBetween(inicio, fim);
    }
}