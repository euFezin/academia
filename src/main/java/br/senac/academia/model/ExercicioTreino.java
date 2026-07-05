package br.senac.academia.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "exercicios_treino")
public class ExercicioTreino {

    @EmbeddedId
    private ExercicioTreinoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("treino")
    @JoinColumn(name = "treino_id", nullable = false)
    @JsonIgnoreProperties({"instrutor", "aluno", "exercicios"})
    private Treino treino;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("exercicio")
    @JoinColumn(name = "exercicio_id", nullable = false)
    private Exercicio exercicio;

    @Column(nullable = false)
    private Integer series;

    @Column(nullable = false)
    private Integer repeticoes;

    @Column(name = "carga_estimada")
    private BigDecimal cargaEstimada;
}