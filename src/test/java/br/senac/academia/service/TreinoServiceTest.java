package br.senac.academia.service;

import br.senac.academia.model.*;
import br.senac.academia.model.enums.TipoTreino;
import br.senac.academia.repository.ExercicioTreinoRepository;
import br.senac.academia.repository.TreinoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - TreinoService")
class TreinoServiceTest {

    @Mock
    private TreinoRepository treinoRepository;

    @Mock
    private ExercicioTreinoRepository exercicioTreinoRepository;

    @InjectMocks
    private TreinoService treinoService;

    private Treino treino;
    private Aluno aluno;
    private Instrutor instrutor;
    private Exercicio exercicio;
    private ExercicioTreino exercicioTreino;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("João Silva");

        instrutor = new Instrutor();
        instrutor.setId(1L);
        instrutor.setNome("Carlos Instrutor");

        exercicio = new Exercicio();
        exercicio.setId(1L);
        exercicio.setNome("Supino");
        exercicio.setGrupoMuscular("Peito");

        treino = new Treino();
        treino.setId(1L);
        treino.setNome("Treino A - Peito e Tríceps");
        treino.setDescricao("Treino focado em peito e tríceps");
        treino.setTipo(TipoTreino.MUSCULACAO);
        treino.setDataCriacao(LocalDate.now());
        treino.setDataValidade(LocalDate.now().plusMonths(3));
        treino.setAtivo(true);
        treino.setAluno(aluno);
        treino.setInstrutor(instrutor);

        exercicioTreino = new ExercicioTreino();
        exercicioTreino.setExercicio(exercicio);
        exercicioTreino.setSeries(3);
        exercicioTreino.setRepeticoes(12);
        exercicioTreino.setCargaEstimada(new BigDecimal("60.0"));

        treino.setExercicios(Arrays.asList(exercicioTreino));
    }

    @Test
    @DisplayName("Deve listar todos os treinos")
    void deveListarTodosTreinos() {
        List<Treino> treinos = Arrays.asList(treino);
        when(treinoRepository.findAll()).thenReturn(treinos);

        List<Treino> resultado = treinoService.findAll();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Treino A - Peito e Tríceps", resultado.get(0).getNome());
        verify(treinoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar treino por ID quando existir")
    void deveBuscarTreinoPorIdQuandoExistir() {
        when(treinoRepository.findById(1L)).thenReturn(Optional.of(treino));

        Optional<Treino> resultado = treinoService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Treino A - Peito e Tríceps", resultado.get().getNome());
        verify(treinoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando treino não existir")
    void deveRetornarOptionalVazioQuandoTreinoNaoExistir() {
        when(treinoRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Treino> resultado = treinoService.findById(999L);

        assertFalse(resultado.isPresent());
        verify(treinoRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve salvar treino com sucesso")
    void deveSalvarTreinoComSucesso() {
        // Arrange
        when(treinoRepository.save(any(Treino.class))).thenReturn(treino);

        // Act
        Treino resultado = treinoService.save(treino);

        // Assert
        assertNotNull(resultado);
        assertEquals("Treino A - Peito e Tríceps", resultado.getNome());
        verify(treinoRepository, times(1)).save(treino);
    }

    @Test
    @DisplayName("Deve deletar treino por ID")
    void deveDeletarTreinoPorId() {
        // Arrange
        doNothing().when(treinoRepository).deleteById(1L);

        // Act
        treinoService.deleteById(1L);

        // Assert
        verify(treinoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve buscar treinos por aluno")
    void deveBuscarTreinosPorAluno() {
        // Arrange
        List<Treino> treinos = Arrays.asList(treino);
        when(treinoRepository.findByAlunoId(1L)).thenReturn(treinos);

        // Act
        List<Treino> resultado = treinoService.findByAluno(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(treinoRepository, times(1)).findByAlunoId(1L);
    }

    @Test
    @DisplayName("Deve buscar treinos por instrutor")
    void deveBuscarTreinosPorInstrutor() {
        // Arrange
        List<Treino> treinos = Arrays.asList(treino);
        when(treinoRepository.findByInstrutorId(1L)).thenReturn(treinos);

        // Act
        List<Treino> resultado = treinoService.findByInstrutor(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(treinoRepository, times(1)).findByInstrutorId(1L);
    }

    @Test
    @DisplayName("Deve buscar treinos ativos")
    void deveBuscarTreinosAtivos() {
        // Arrange
        List<Treino> treinos = Arrays.asList(treino);
        when(treinoRepository.findByAtivoTrue()).thenReturn(treinos);

        // Act
        List<Treino> resultado = treinoService.findByAtivoTrue();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getAtivo());
        verify(treinoRepository, times(1)).findByAtivoTrue();
    }

    @Test
    @DisplayName("Deve buscar treinos vencidos")
    void deveBuscarTreinosVencidos() {
        // Arrange
        Treino treinoVencido = new Treino();
        treinoVencido.setId(2L);
        treinoVencido.setDataValidade(LocalDate.now().minusDays(1));
        treinoVencido.setAtivo(true);

        List<Treino> treinosVencidos = Arrays.asList(treinoVencido);
        when(treinoRepository.findTreinosVencidos()).thenReturn(treinosVencidos);

        // Act
        List<Treino> resultado = treinoService.findTreinosVencidos();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(treinoRepository, times(1)).findTreinosVencidos();
    }

    @Test
    @DisplayName("Deve contar treinos ativos por aluno")
    void deveContarTreinosAtivosPorAluno() {
        // Arrange
        Long totalEsperado = 3L;
        when(treinoRepository.countTreinosAtivosPorAluno(1L)).thenReturn(totalEsperado);

        // Act
        Long resultado = treinoService.countTreinosAtivosPorAluno(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(totalEsperado, resultado);
        verify(treinoRepository, times(1)).countTreinosAtivosPorAluno(1L);
    }

    @Test
    @DisplayName("Deve buscar treinos por nome do exercício")
    void deveBuscarTreinosPorNomeExercicio() {
        // Arrange
        List<Treino> treinos = Arrays.asList(treino);
        when(treinoRepository.buscarTreinosPorNomeExercicio("Supino")).thenReturn(treinos);

        // Act
        List<Treino> resultado = treinoService.buscarTreinosPorNomeExercicio("Supino");

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(treinoRepository, times(1)).buscarTreinosPorNomeExercicio("Supino");
    }

    @Test
    @DisplayName("Deve criar treino com exercícios em transação")
    void deveCriarTreinoComExercicios() {
        // Arrange
        Treino novoTreino = new Treino();
        novoTreino.setNome("Novo Treino");
        novoTreino.setAluno(aluno);
        novoTreino.setInstrutor(instrutor);
        novoTreino.setDataCriacao(LocalDate.now());
        novoTreino.setDataValidade(LocalDate.now().plusMonths(3));
        novoTreino.setTipo(TipoTreino.MUSCULACAO);
        novoTreino.setAtivo(true);
        novoTreino.setExercicios(Arrays.asList(exercicioTreino));

        Treino treinoSalvo = new Treino();
        treinoSalvo.setId(1L);
        treinoSalvo.setNome("Novo Treino");

        when(treinoRepository.save(any(Treino.class))).thenReturn(treinoSalvo);
        when(exercicioTreinoRepository.save(any(ExercicioTreino.class))).thenReturn(exercicioTreino);

        // Act
        Treino resultado = treinoService.criarTreinoComExercicios(novoTreino);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(treinoRepository, times(1)).save(any(Treino.class));
        verify(exercicioTreinoRepository, times(1)).save(any(ExercicioTreino.class));
    }

    @Test
    @DisplayName("Deve criar treino sem exercícios")
    void deveCriarTreinoSemExercicios() {
        // Arrange
        Treino novoTreino = new Treino();
        novoTreino.setNome("Treino Simples");
        novoTreino.setAluno(aluno);
        novoTreino.setInstrutor(instrutor);
        novoTreino.setDataCriacao(LocalDate.now());
        novoTreino.setDataValidade(LocalDate.now().plusMonths(3));
        novoTreino.setTipo(TipoTreino.MUSCULACAO);
        novoTreino.setAtivo(true);
        novoTreino.setExercicios(null);

        Treino treinoSalvo = new Treino();
        treinoSalvo.setId(1L);
        treinoSalvo.setNome("Treino Simples");

        when(treinoRepository.save(any(Treino.class))).thenReturn(treinoSalvo);

        // Act
        Treino resultado = treinoService.criarTreinoComExercicios(novoTreino);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(treinoRepository, times(1)).save(any(Treino.class));
        verify(exercicioTreinoRepository, never()).save(any(ExercicioTreino.class));
    }
}

