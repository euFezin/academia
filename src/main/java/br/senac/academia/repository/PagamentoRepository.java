package br.senac.academia.repository;

import br.senac.academia.model.Pagamento;
import br.senac.academia.model.enums.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    List<Pagamento> findByAlunoId(Long alunoId);
    List<Pagamento> findByStatus(StatusPagamento status);
    List<Pagamento> findByDataVencimentoBetween(LocalDate inicio, LocalDate fim);

    @Query("SELECT p FROM Pagamento p WHERE p.dataVencimento < CURRENT_DATE AND p.status = 'PENDENTE'")
    List<Pagamento> findPagamentosEmAtraso();

    @Query("SELECT SUM(p.valor) FROM Pagamento p WHERE p.status = 'PAGO' AND p.dataPagamento BETWEEN :inicio AND :fim")
    Double calcularTotalRecebidoPeriodo(LocalDate inicio, LocalDate fim);

    @Query("SELECT p FROM Pagamento p JOIN p.plano pl WHERE p.dataVencimento < CURRENT_DATE AND p.status = 'PENDENTE' AND pl.nome LIKE %:nomePlano%")
    List<Pagamento> findAtrasadosPorNomePlano(String nomePlano);
}