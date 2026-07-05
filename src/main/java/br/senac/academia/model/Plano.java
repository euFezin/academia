package br.senac.academia.model;

import br.senac.academia.model.enums.TipoPlano;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "planos")
public class Plano {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do plano é obrigatório")
    @Column(nullable = false, unique = true, length = 50)
    private String nome;

    @NotBlank(message = "Descrição é obrigatória")
    @Column(nullable = false, length = 200)
    private String descricao;

    @NotNull(message = "Valor mensal é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Valor deve ser maior que zero")
    @Column(name = "valor_mensal", nullable = false)
    private BigDecimal valorMensal;

    @NotNull(message = "Duração em meses é obrigatória")
    @Column(name = "duracao_meses", nullable = false)
    private Integer duracaoMeses;

    @Column(name = "aulas_semana")
    private Integer aulasPorSemana;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPlano tipo = TipoPlano.BASICO;

    @Column(nullable = false)
    private Boolean ativo = true;

    @OneToMany(mappedBy = "plano", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Aluno> alunos = new ArrayList<>();

    @OneToMany(mappedBy = "plano", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Pagamento> pagamentos = new ArrayList<>();

    public BigDecimal calcularValorTotal() {
        return valorMensal.multiply(BigDecimal.valueOf(duracaoMeses));
    }

    public boolean isPlanoLongoPrazo() {
        return duracaoMeses != null && duracaoMeses >= 6;
    }

    public void desativar() {
        this.ativo = false;
    }

    public void ativar() {
        this.ativo = true;
    }
}
