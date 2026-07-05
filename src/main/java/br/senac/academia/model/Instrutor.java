package br.senac.academia.model;

import br.senac.academia.model.enums.StatusInstrutor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "instrutores")
public class Instrutor {

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

    @NotBlank(message = "Especialidade é obrigatória")
    @Column(nullable = false, length = 100)
    private String especialidade;

    @Column(name = "numero_registro", unique = true, length = 20)
    private String numeroRegistro;

    @Column(name = "salario")
    private Double salario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusInstrutor status = StatusInstrutor.ATIVO;

    @OneToMany(mappedBy = "instrutor", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Aluno> alunosOrientados = new ArrayList<>();

    @OneToMany(mappedBy = "instrutor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Aula> aulasMinistradas = new ArrayList<>();

    public int calcularIdade() {
        return LocalDate.now().getYear() - this.dataNascimento.getYear();
    }

    public boolean isAtivo() {
        return this.status == StatusInstrutor.ATIVO;
    }

    public int getQuantidadeAlunos() {
        return this.alunosOrientados != null ? this.alunosOrientados.size() : 0;
    }

    public void adicionarAluno(Aluno aluno) {
        if (this.alunosOrientados == null) {
            this.alunosOrientados = new ArrayList<>();
        }
        this.alunosOrientados.add(aluno);
        aluno.setInstrutor(this);
    }
}