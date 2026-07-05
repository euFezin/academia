package br.senac.academia.controller;

import br.senac.academia.model.Aluno;
import br.senac.academia.model.Plano;
import br.senac.academia.model.enums.StatusAluno;
import br.senac.academia.model.enums.TipoPlano;
import br.senac.academia.repository.AlunoRepository;
import br.senac.academia.repository.PlanoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - AlunoController")
class AlunoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private PlanoRepository planoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Plano plano;
    private Aluno aluno;

    @BeforeEach
    void setUp() {
        alunoRepository.deleteAll();
        planoRepository.deleteAll();

        plano = new Plano();
        plano.setNome("Plano Básico");
        plano.setDescricao("Plano básico de academia");
        plano.setValorMensal(new BigDecimal("99.90"));
        plano.setDuracaoMeses(1);
        plano.setTipo(TipoPlano.BASICO);
        plano.setAtivo(true);
        plano = planoRepository.save(plano);

        aluno = new Aluno();
        aluno.setNome("João Silva");
        aluno.setEmail("joao@email.com");
        aluno.setCpf("123.456.789-00");
        aluno.setDataNascimento(LocalDate.of(1990, 1, 1));
        aluno.setTelefone("(11) 99999-9999");
        aluno.setEndereco("Rua Teste, 123");
        aluno.setStatus(StatusAluno.ATIVO);
        aluno.setPlano(plano);
        aluno = alunoRepository.save(aluno);
    }

    @Test
    @DisplayName("Deve listar todos os alunos")
    void deveListarTodosAlunos() throws Exception {
        mockMvc.perform(get("/api/alunos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value("João Silva"));
    }

    @Test
    @DisplayName("Deve buscar aluno por ID quando existir")
    void deveBuscarAlunoPorIdQuandoExistir() throws Exception {
        mockMvc.perform(get("/api/alunos/{id}", aluno.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(aluno.getId()))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    @DisplayName("Deve retornar 404 quando aluno não existir")
    void deveRetornar404QuandoAlunoNaoExistir() throws Exception {
        mockMvc.perform(get("/api/alunos/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve criar novo aluno com sucesso")
    void deveCriarNovoAlunoComSucesso() throws Exception {
        Aluno novoAluno = new Aluno();
        novoAluno.setNome("Maria Santos");
        novoAluno.setEmail("maria@email.com");
        novoAluno.setCpf("987.654.321-00");
        novoAluno.setDataNascimento(LocalDate.of(1995, 5, 15));
        novoAluno.setTelefone("(11) 88888-8888");
        novoAluno.setEndereco("Rua Nova, 456");
        novoAluno.setStatus(StatusAluno.ATIVO);
        novoAluno.setPlano(plano);

        String alunoJson = objectMapper.writeValueAsString(novoAluno);

        mockMvc.perform(post("/api/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(alunoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria Santos"))
                .andExpect(jsonPath("$.email").value("maria@email.com"));

        List<Aluno> alunos = alunoRepository.findAll();
        assertEquals(2, alunos.size());
    }

    @Test
    @DisplayName("Deve atualizar aluno existente")
    void deveAtualizarAlunoExistente() throws Exception {
        Aluno alunoAtualizado = new Aluno();
        alunoAtualizado.setNome("João Silva Atualizado");
        alunoAtualizado.setEmail("joao.novo@email.com");
        alunoAtualizado.setTelefone("(11) 77777-7777");
        alunoAtualizado.setEndereco("Rua Atualizada, 789");
        alunoAtualizado.setStatus(StatusAluno.INATIVO);
        alunoAtualizado.setPlano(plano);

        String alunoJson = objectMapper.writeValueAsString(alunoAtualizado);

        mockMvc.perform(put("/api/alunos/{id}", aluno.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(alunoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva Atualizado"))
                .andExpect(jsonPath("$.email").value("joao.novo@email.com"));

        Aluno alunoAtualizadoNoBanco = alunoRepository.findById(aluno.getId()).orElseThrow();
        assertEquals("João Silva Atualizado", alunoAtualizadoNoBanco.getNome());
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar aluno inexistente")
    void deveRetornar404AoAtualizarAlunoInexistente() throws Exception {
        Aluno alunoAtualizado = new Aluno();
        alunoAtualizado.setNome("Aluno Inexistente");
        alunoAtualizado.setPlano(plano);

        String alunoJson = objectMapper.writeValueAsString(alunoAtualizado);

        mockMvc.perform(put("/api/alunos/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(alunoJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar aluno existente")
    void deveDeletarAlunoExistente() throws Exception {
        mockMvc.perform(delete("/api/alunos/{id}", aluno.getId()))
                .andExpect(status().isNoContent());

        assertFalse(alunoRepository.findById(aluno.getId()).isPresent());
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar deletar aluno inexistente")
    void deveRetornar404AoDeletarAlunoInexistente() throws Exception {
        mockMvc.perform(delete("/api/alunos/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve buscar alunos por status")
    void deveBuscarAlunosPorStatus() throws Exception {
        Aluno alunoInativo = new Aluno();
        alunoInativo.setNome("Aluno Inativo");
        alunoInativo.setEmail("inativo@email.com");
        alunoInativo.setCpf("111.222.333-44");
        alunoInativo.setDataNascimento(LocalDate.of(1990, 1, 1));
        alunoInativo.setTelefone("(11) 11111-1111");
        alunoInativo.setStatus(StatusAluno.INATIVO);
        alunoInativo.setPlano(plano);
        alunoRepository.save(alunoInativo);

        mockMvc.perform(get("/api/alunos/status/{status}", StatusAluno.ATIVO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("ATIVO"));

        mockMvc.perform(get("/api/alunos/status/{status}", StatusAluno.INATIVO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("INATIVO"));
    }

    @Test
    @DisplayName("Deve buscar alunos por plano")
    void deveBuscarAlunosPorPlano() throws Exception {
        mockMvc.perform(get("/api/alunos/plano/{planoId}", plano.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value("João Silva"));
    }

    @Test
    @DisplayName("Deve retornar média de idade dos alunos")
    void deveRetornarMediaIdadeAlunos() throws Exception {
        Aluno aluno2 = new Aluno();
        aluno2.setNome("Pedro Costa");
        aluno2.setEmail("pedro@email.com");
        aluno2.setCpf("555.666.777-88");
        aluno2.setDataNascimento(LocalDate.of(1985, 3, 10));
        aluno2.setTelefone("(11) 22222-2222");
        aluno2.setStatus(StatusAluno.ATIVO);
        aluno2.setPlano(plano);
        alunoRepository.save(aluno2);

        mockMvc.perform(get("/api/alunos/estatisticas/media-idade"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());
    }

    @Test
    @DisplayName("Deve retornar total de alunos ativos")
    void deveRetornarTotalAlunosAtivos() throws Exception {
        mockMvc.perform(get("/api/alunos/estatisticas/total-ativos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber())
                .andExpect(jsonPath("$").value(1));
    }
}

