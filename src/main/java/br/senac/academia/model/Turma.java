package br.senac.academia.model;

import br.senac.academia.model.enums.NivelDificuldade;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "turmas")
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nome;

    @Column(nullable = false, length = 100)
    private String descricao;

    @Column(name = "capacidade_maxima")
    private Integer capacidadeMaxima;

    @Column(name = "idade_minima")
    private Integer idadeMinima;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelDificuldade nivel = NivelDificuldade.INICIANTE;

    @Column(nullable = false)
    private Boolean ativo = true;

    @OneToMany(mappedBy = "turma", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Aula> aulas = new ArrayList<>();


    @ManyToMany(mappedBy = "turmas")
    @JsonIgnoreProperties("turmas")
    private List<Aluno> alunos = new ArrayList<>();
}