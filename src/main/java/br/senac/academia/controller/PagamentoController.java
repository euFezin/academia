package br.senac.academia.controller;

import br.senac.academia.model.Pagamento;
import br.senac.academia.model.enums.StatusPagamento;
import br.senac.academia.service.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @GetMapping
    public List<Pagamento> getAllPagamentos() {
        return pagamentoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pagamento> getPagamentoById(@PathVariable Long id) {
        Optional<Pagamento> pagamento = pagamentoService.findById(id);
        return pagamento.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Pagamento createPagamento(@RequestBody Pagamento pagamento) {
        return pagamentoService.save(pagamento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pagamento> updatePagamento(@PathVariable Long id, @RequestBody Pagamento pagamentoDetails) {
        Optional<Pagamento> pagamentoOpt = pagamentoService.findById(id);
        if (pagamentoOpt.isPresent()) {
            Pagamento pagamento = pagamentoOpt.get();
            pagamento.setDataVencimento(pagamentoDetails.getDataVencimento());
            pagamento.setDataPagamento(pagamentoDetails.getDataPagamento());
            pagamento.setValor(pagamentoDetails.getValor());
            pagamento.setStatus(pagamentoDetails.getStatus());
            pagamento.setMetodo(pagamentoDetails.getMetodo());
            return ResponseEntity.ok(pagamentoService.save(pagamento));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePagamento(@PathVariable Long id) {
        if (pagamentoService.findById(id).isPresent()) {
            pagamentoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/aluno/{alunoId}")
    public List<Pagamento> getPagamentosByAluno(@PathVariable Long alunoId) {
        return pagamentoService.findByAluno(alunoId);
    }

    @GetMapping("/status/{status}")
    public List<Pagamento> getPagamentosByStatus(@PathVariable StatusPagamento status) {
        return pagamentoService.findByStatus(status);
    }

    @GetMapping("/em-atraso/plano")
    public List<Pagamento> getAtrasadosPorPlano(@RequestParam("nomePlano") String nomePlano) {
        return pagamentoService.findPagamentosAtrasadosPorPlano(nomePlano);
    }

    @PutMapping("/{id}/pagar")
    public ResponseEntity<Pagamento> marcarComoPago(@PathVariable Long id) {
        Pagamento pagamento = pagamentoService.marcarComoPago(id);
        return pagamento != null ? ResponseEntity.ok(pagamento) : ResponseEntity.notFound().build();
    }

    @GetMapping("/total-recebido")
    public ResponseEntity<Double> getTotalRecebidoPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        Double total = pagamentoService.calcularTotalRecebidoPeriodo(inicio, fim);
        return ResponseEntity.ok(total);
    }
}