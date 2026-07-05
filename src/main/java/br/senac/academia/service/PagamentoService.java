package br.senac.academia.service;

import br.senac.academia.model.Pagamento;
import br.senac.academia.model.enums.StatusPagamento;
import br.senac.academia.repository.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    public List<Pagamento> findAll() {
        return pagamentoRepository.findAll();
    }

    public Optional<Pagamento> findById(Long id) {
        return pagamentoRepository.findById(id);
    }

    public Pagamento save(Pagamento pagamento) {
        return pagamentoRepository.save(pagamento);
    }

    public void deleteById(Long id) {
        pagamentoRepository.deleteById(id);
    }

    public List<Pagamento> findByAluno(Long alunoId) {
        return pagamentoRepository.findByAlunoId(alunoId);
    }

    public List<Pagamento> findByStatus(StatusPagamento status) {
        return pagamentoRepository.findByStatus(status);
    }

    public List<Pagamento> findPagamentosEmAtraso() {
        return pagamentoRepository.findPagamentosEmAtraso();
    }

    public List<Pagamento> findByPeriodoVencimento(LocalDate inicio, LocalDate fim) {
        return pagamentoRepository.findByDataVencimentoBetween(inicio, fim);
    }

    public Double calcularTotalRecebidoPeriodo(LocalDate inicio, LocalDate fim) {
        return pagamentoRepository.calcularTotalRecebidoPeriodo(inicio, fim);
    }

    public List<Pagamento> findPagamentosAtrasadosPorPlano(String nomePlano) {
        return pagamentoRepository.findAtrasadosPorNomePlano(nomePlano);
    }

    public Pagamento marcarComoPago(Long id) {
        Optional<Pagamento> pagamentoOpt = pagamentoRepository.findById(id);
        if (pagamentoOpt.isPresent()) {
            Pagamento pagamento = pagamentoOpt.get();
            pagamento.setStatus(StatusPagamento.PAGO);
            pagamento.setDataPagamento(LocalDate.now());
            return pagamentoRepository.save(pagamento);
        }
        return null;
    }
}