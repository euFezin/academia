package br.senac.academia.service;

import br.senac.academia.model.Plano;
import br.senac.academia.repository.PlanoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanoService {

    @Autowired
    private PlanoRepository planoRepository;

    public List<Plano> findAll() {
        return planoRepository.findAll();
    }

    public Optional<Plano> findById(Long id) {
        return planoRepository.findById(id);
    }

    public Plano save(Plano plano) {
        return planoRepository.save(plano);
    }

    public void deleteById(Long id) {
        planoRepository.deleteById(id);
    }

    public List<Plano> findPlanosAtivos() {
        return planoRepository.findByAtivoTrue();
    }

    public List<Plano> findByFaixaPreco(Double min, Double max) {
        return planoRepository.findByValorMensalBetween(min, max);
    }

    public Long countAlunosNoPlano(Long planoId) {
        return planoRepository.countAlunosByPlano(planoId);
    }

    public Optional<Plano> findByNome(String nome) {
        return planoRepository.findByNome(nome);
    }
}