package br.senac.academia.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "avaliacoes_fisicas")
public class AvaliacaoFisica {

    @EmbeddedId
    private AvaliacaoFisicaId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("aluno") // Mapeia o campo 'aluno' da classe AvaliacaoFisicaId
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @Column(nullable = false)
    private BigDecimal peso;

    @Column(nullable = false)
    private BigDecimal altura;

    @Column(name = "percentual_gordura")
    private BigDecimal percentualGordura;

    @Column(name = "massa_muscular")
    private BigDecimal massaMuscular;

    @Column(name = "circunferencia_cintura")
    private BigDecimal circunferenciaCintura;

    @Column(name = "circunferencia_quadril")
    private BigDecimal circunferenciaQuadril;

    @Column(length = 500)
    private String observacoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instrutor_id", nullable = false)
    private Instrutor instrutor;
}