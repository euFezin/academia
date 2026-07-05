package br.senac.academia.model;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class AvaliacaoFisicaId implements Serializable {

    private Long aluno;

    private LocalDate dataAvaliacao;
}