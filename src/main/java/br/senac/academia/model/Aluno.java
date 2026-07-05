package br.senac.academia.model;

import br.senac.academia.model.enums.StatusAluno;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "alunos")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "CPF é obrigatório")
    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve ser no passado")
    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @NotBlank(message = "Telefone é obrigatório")
    @Column(nullable = false, length = 15)
    private String telefone;

    @Column(name = "data_matricula")
    private LocalDateTime dataMatricula;

    @Column(length = 200)
    private String endereco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAluno status = StatusAluno.ATIVO;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plano_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "alunos"})
    private Plano plano;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instrutor_id")
    @JsonIgnore
    private Instrutor instrutor;

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"aluno", "instrutor", "id"})
    private List<AvaliacaoFisica> avaliacoesFisicas;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(
            name = "aluno_turma",
            joinColumns = @JoinColumn(name = "aluno_id"),
            inverseJoinColumns = @JoinColumn(name = "turma_id")
    )
    @JsonIgnoreProperties("alunos") // Evita recursão infinita na serialização JSON
    private List<Turma> turmas = new ArrayList<>();

    public int calcularIdade() {
        return LocalDate.now().getYear() - this.dataNascimento.getYear();
    }

    public boolean isAtivo() {
        return this.status == StatusAluno.ATIVO;
    }

    @PrePersist
    public void prePersist() {
        this.dataMatricula = LocalDateTime.now();
    }
}