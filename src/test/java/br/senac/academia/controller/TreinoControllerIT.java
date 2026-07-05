package br.senac.academia.controller;

import br.senac.academia.model.*;
import br.senac.academia.model.enums.StatusAluno;
import br.senac.academia.model.enums.TipoPlano;
import br.senac.academia.model.enums.TipoTreino;
import br.senac.academia.repository.*;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - TreinoController")
class TreinoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TreinoRepository treinoRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private InstrutorRepository instrutorRepository;

    @Autowired
    private PlanoRepository planoRepository;

    @Autowired
    private ExercicioRepository exercicioRepository;

    @Autowired
    private ExercicioTreinoRepository exercicioTreinoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Aluno aluno;
    private Instrutor instrutor;
    private Plano plano;
    private Exercicio exercicio;
    private Treino treino;

    @BeforeEach
    void setUp() {
        exercicioTreinoRepository.deleteAll();
        treinoRepository.deleteAll();
        alunoRepository.deleteAll();
        instrutorRepository.deleteAll();
        planoRepository.deleteAll();
        exercicioRepository.deleteAll();

        plano = new Plano();
        plano.setNome("Plano Básico");
        plano.setDescricao("Plano básico");
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
        aluno.setStatus(StatusAluno.ATIVO);
        aluno.setPlano(plano);
        aluno = alunoRepository.save(aluno);

        instrutor = new Instrutor();
        instrutor.setNome("Carlos Instrutor");
        instrutor.setEmail("carlos@email.com");
        instrutor.setCpf("987.654.321-00");
        instrutor.setDataNascimento(LocalDate.of(1985, 5, 10));
        instrutor.setTelefone("(11) 88888-8888");
        instrutor.setEspecialidade("Musculação");
        instrutor = instrutorRepository.save(instrutor);

        exercicio = new Exercicio();
        exercicio.setNome("Supino");
        exercicio.setGrupoMuscular("Peito");
        exercicio.setInstrucoes("Deite no banco e empurre a barra");
        exercicio = exercicioRepository.save(exercicio);

        treino = new Treino();
        treino.setNome("Treino A - Peito e Tríceps");
        treino.setDescricao("Treino focado em peito e tríceps");
        treino.setTipo(TipoTreino.MUSCULACAO);
        treino.setDataCriacao(LocalDate.now());
        treino.setDataValidade(LocalDate.now().plusMonths(3));
        treino.setAtivo(true);
        treino.setAluno(aluno);
        treino.setInstrutor(instrutor);
        treino = treinoRepository.save(treino);
    }

    @Test
    @DisplayName("Deve listar todos os treinos")
    void deveListarTodosTreinos() throws Exception {
        mockMvc.perform(get("/api/treinos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value("Treino A - Peito e Tríceps"));
    }

    @Test
    @DisplayName("Deve buscar treino por ID quando existir")
    void deveBuscarTreinoPorIdQuandoExistir() throws Exception {
        mockMvc.perform(get("/api/treinos/{id}", treino.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(treino.getId()))
                .andExpect(jsonPath("$.nome").value("Treino A - Peito e Tríceps"));
    }

    @Test
    @DisplayName("Deve retornar 404 quando treino não existir")
    void deveRetornar404QuandoTreinoNaoExistir() throws Exception {
        mockMvc.perform(get("/api/treinos/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve criar novo treino com sucesso")
    void deveCriarNovoTreinoComSucesso() throws Exception {
        Treino novoTreino = new Treino();
        novoTreino.setNome("Treino B - Costas e Bíceps");
        novoTreino.setDescricao("Treino focado em costas e bíceps");
        novoTreino.setTipo(TipoTreino.MUSCULACAO);
        novoTreino.setDataCriacao(LocalDate.now());
        novoTreino.setDataValidade(LocalDate.now().plusMonths(3));
        novoTreino.setAtivo(true);
        novoTreino.setAluno(aluno);
        novoTreino.setInstrutor(instrutor);

        String treinoJson = objectMapper.writeValueAsString(novoTreino);

        mockMvc.perform(post("/api/treinos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(treinoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Treino B - Costas e Bíceps"));

        List<Treino> treinos = treinoRepository.findAll();
        assertEquals(2, treinos.size());
    }

    @Test
    @DisplayName("Deve criar treino com exercícios")
    void deveCriarTreinoComExercicios() throws Exception {
        Treino novoTreino = new Treino();
        novoTreino.setNome("Treino Completo");
        novoTreino.setDescricao("Treino com exercícios");
        novoTreino.setTipo(TipoTreino.MUSCULACAO);
        novoTreino.setDataCriacao(LocalDate.now());
        novoTreino.setDataValidade(LocalDate.now().plusMonths(3));
        novoTreino.setAtivo(true);
        novoTreino.setAluno(aluno);
        novoTreino.setInstrutor(instrutor);

        ExercicioTreino exercicioTreino = new ExercicioTreino();
        exercicioTreino.setExercicio(exercicio);
        exercicioTreino.setSeries(3);
        exercicioTreino.setRepeticoes(12);
        exercicioTreino.setCargaEstimada(new BigDecimal("60.0"));

        List<ExercicioTreino> exercicios = new ArrayList<>();
        exercicios.add(exercicioTreino);
        novoTreino.setExercicios(exercicios);

        String treinoJson = objectMapper.writeValueAsString(novoTreino);

        mockMvc.perform(post("/api/treinos/com-exercicios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(treinoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Treino Completo"));

        List<Treino> treinos = treinoRepository.findAll();
        assertEquals(2, treinos.size());
    }

    @Test
    @DisplayName("Deve atualizar treino existente")
    void deveAtualizarTreinoExistente() throws Exception {
        Treino treinoAtualizado = new Treino();
        treinoAtualizado.setNome("Treino A - Atualizado");
        treinoAtualizado.setDescricao("Descrição atualizada");
        treinoAtualizado.setTipo(TipoTreino.CARDIO);
        treinoAtualizado.setDataValidade(LocalDate.now().plusMonths(6));
        treinoAtualizado.setAtivo(false);
        treinoAtualizado.setAluno(aluno);
        treinoAtualizado.setInstrutor(instrutor);

        String treinoJson = objectMapper.writeValueAsString(treinoAtualizado);

        mockMvc.perform(put("/api/treinos/{id}", treino.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(treinoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Treino A - Atualizado"))
                .andExpect(jsonPath("$.tipo").value("CARDIO"));

        Treino treinoAtualizadoNoBanco = treinoRepository.findById(treino.getId()).orElseThrow();
        assertEquals("Treino A - Atualizado", treinoAtualizadoNoBanco.getNome());
        assertFalse(treinoAtualizadoNoBanco.getAtivo());
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar treino inexistente")
    void deveRetornar404AoAtualizarTreinoInexistente() throws Exception {
        Treino treinoAtualizado = new Treino();
        treinoAtualizado.setNome("Treino Inexistente");

        String treinoJson = objectMapper.writeValueAsString(treinoAtualizado);

        mockMvc.perform(put("/api/treinos/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(treinoJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar treino existente")
    void deveDeletarTreinoExistente() throws Exception {
        mockMvc.perform(delete("/api/treinos/{id}", treino.getId()))
                .andExpect(status().isNoContent());

        assertFalse(treinoRepository.findById(treino.getId()).isPresent());
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar deletar treino inexistente")
    void deveRetornar404AoDeletarTreinoInexistente() throws Exception {
        mockMvc.perform(delete("/api/treinos/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve buscar treinos por aluno")
    void deveBuscarTreinosPorAluno() throws Exception {
        mockMvc.perform(get("/api/treinos/aluno/{alunoId}", aluno.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value("Treino A - Peito e Tríceps"));
    }

    @Test
    @DisplayName("Deve buscar treinos por instrutor")
    void deveBuscarTreinosPorInstrutor() throws Exception {
        mockMvc.perform(get("/api/treinos/instrutor/{instrutorId}", instrutor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value("Treino A - Peito e Tríceps"));
    }

    @Test
    @DisplayName("Deve buscar treinos ativos")
    void deveBuscarTreinosAtivos() throws Exception {
        mockMvc.perform(get("/api/treinos/ativos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].ativo").value(true));
    }

    @Test
    @DisplayName("Deve buscar treinos vencidos")
    void deveBuscarTreinosVencidos() throws Exception {
        Treino treinoVencido = new Treino();
        treinoVencido.setNome("Treino Vencido");
        treinoVencido.setTipo(TipoTreino.MUSCULACAO);
        treinoVencido.setDataCriacao(LocalDate.now().minusMonths(4));
        treinoVencido.setDataValidade(LocalDate.now().minusDays(1));
        treinoVencido.setAtivo(true);
        treinoVencido.setAluno(aluno);
        treinoVencido.setInstrutor(instrutor);
        treinoRepository.save(treinoVencido);

        mockMvc.perform(get("/api/treinos/vencidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Deve retornar total de treinos ativos por aluno")
    void deveRetornarTotalTreinosAtivosPorAluno() throws Exception {
        mockMvc.perform(get("/api/treinos/aluno/{alunoId}/total-ativos", aluno.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber())
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    @DisplayName("Deve buscar treinos por nome do exercício")
    void deveBuscarTreinosPorNomeExercicio() throws Exception {
        Treino treinoComExercicio = new Treino();
        treinoComExercicio.setNome("Treino com Supino");
        treinoComExercicio.setTipo(TipoTreino.MUSCULACAO);
        treinoComExercicio.setDataCriacao(LocalDate.now());
        treinoComExercicio.setDataValidade(LocalDate.now().plusMonths(3));
        treinoComExercicio.setAtivo(true);
        treinoComExercicio.setAluno(aluno);
        treinoComExercicio.setInstrutor(instrutor);
        treinoComExercicio = treinoRepository.save(treinoComExercicio);

        ExercicioTreino exercicioTreino = new ExercicioTreino();
        ExercicioTreinoId id = new ExercicioTreinoId(treinoComExercicio.getId(), exercicio.getId());
        exercicioTreino.setId(id);
        exercicioTreino.setTreino(treinoComExercicio);
        exercicioTreino.setExercicio(exercicio);
        exercicioTreino.setSeries(3);
        exercicioTreino.setRepeticoes(12);
        exercicioTreinoRepository.save(exercicioTreino);

        mockMvc.perform(get("/api/treinos/buscar-por-exercicio")
                        .param("nome", "Supino"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Deve retornar 204 quando não encontrar treinos por exercício")
    void deveRetornar204QuandoNaoEncontrarTreinosPorExercicio() throws Exception {
        mockMvc.perform(get("/api/treinos/buscar-por-exercicio")
                        .param("nome", "ExercicioInexistente"))
                .andExpect(status().isNoContent());
    }
}

