package br.senac.academia.service;

import br.senac.academia.model.Aluno;
import br.senac.academia.model.Plano;
import br.senac.academia.model.enums.StatusAluno;
import br.senac.academia.repository.AlunoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - AlunoService")
class AlunoServiceTest {

    @Mock
    private AlunoRepository alunoRepository;

    @InjectMocks
    private AlunoService alunoService;

    private Aluno aluno;
    private Plano plano;

    @BeforeEach
    void setUp() {
        plano = new Plano();
        plano.setId(1L);
        plano.setNome("Plano Básico");

        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("João Silva");
        aluno.setEmail("joao@email.com");
        aluno.setCpf("123.456.789-00");
        aluno.setDataNascimento(LocalDate.of(1990, 1, 1));
        aluno.setTelefone("(11) 99999-9999");
        aluno.setEndereco("Rua Teste, 123");
        aluno.setStatus(StatusAluno.ATIVO);
        aluno.setPlano(plano);
    }

    @Test
    @DisplayName("Deve listar todos os alunos")
    void deveListarTodosAlunos() {
        List<Aluno> alunos = Arrays.asList(aluno);
        when(alunoRepository.findAll()).thenReturn(alunos);

        List<Aluno> resultado = alunoService.findAll();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("João Silva", resultado.get(0).getNome());
        verify(alunoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar aluno por ID quando existir")
    void deveBuscarAlunoPorIdQuandoExistir() {
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));

        Optional<Aluno> resultado = alunoService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals("João Silva", resultado.get().getNome());
        verify(alunoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando aluno não existir")
    void deveRetornarOptionalVazioQuandoAlunoNaoExistir() {
        when(alunoRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Aluno> resultado = alunoService.findById(999L);

        assertFalse(resultado.isPresent());
        verify(alunoRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve salvar aluno com sucesso quando CPF e Email são únicos")
    void deveSalvarAlunoComSucesso() {
        when(alunoRepository.findByCpf(aluno.getCpf())).thenReturn(Optional.empty());
        when(alunoRepository.findByEmail(aluno.getEmail())).thenReturn(Optional.empty());
        when(alunoRepository.save(any(Aluno.class))).thenReturn(aluno);

        Aluno resultado = alunoService.save(aluno);

        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        verify(alunoRepository, times(1)).findByCpf(aluno.getCpf());
        verify(alunoRepository, times(1)).findByEmail(aluno.getEmail());
        verify(alunoRepository, times(1)).save(aluno);
    }

    @Test
    @DisplayName("Deve atualizar aluno existente com mesmo CPF")
    void deveAtualizarAlunoExistenteComMesmoCpf() {
        aluno.setId(1L);
        when(alunoRepository.findByCpf(aluno.getCpf())).thenReturn(Optional.of(aluno));
        when(alunoRepository.findByEmail(aluno.getEmail())).thenReturn(Optional.of(aluno));
        when(alunoRepository.save(any(Aluno.class))).thenReturn(aluno);

        Aluno resultado = alunoService.save(aluno);

        assertNotNull(resultado);
        verify(alunoRepository, times(1)).save(aluno);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar salvar aluno com CPF duplicado")
    void deveLancarExcecaoQuandoCpfDuplicado() {
        Aluno alunoExistente = new Aluno();
        alunoExistente.setId(2L);
        alunoExistente.setCpf("123.456.789-00");

        when(alunoRepository.findByCpf(aluno.getCpf())).thenReturn(Optional.of(alunoExistente));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            alunoService.save(aluno);
        });

        assertEquals("Já existe um aluno cadastrado com este CPF: 123.456.789-00", exception.getMessage());
        verify(alunoRepository, never()).save(any(Aluno.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar salvar aluno com Email duplicado")
    void deveLancarExcecaoQuandoEmailDuplicado() {
        Aluno alunoExistente = new Aluno();
        alunoExistente.setId(2L);
        alunoExistente.setEmail("joao@email.com");

        when(alunoRepository.findByCpf(aluno.getCpf())).thenReturn(Optional.empty());
        when(alunoRepository.findByEmail(aluno.getEmail())).thenReturn(Optional.of(alunoExistente));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            alunoService.save(aluno);
        });

        assertEquals("Já existe um aluno cadastrado com este Email: joao@email.com", exception.getMessage());
        verify(alunoRepository, never()).save(any(Aluno.class));
    }

    @Test
    @DisplayName("Deve deletar aluno por ID")
    void deveDeletarAlunoPorId() {
        doNothing().when(alunoRepository).deleteById(1L);

        alunoService.deleteById(1L);

        verify(alunoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve buscar alunos por status")
    void deveBuscarAlunosPorStatus() {
        List<Aluno> alunosAtivos = Arrays.asList(aluno);
        when(alunoRepository.findByStatus(StatusAluno.ATIVO)).thenReturn(alunosAtivos);

        List<Aluno> resultado = alunoService.findByStatus(StatusAluno.ATIVO);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(alunoRepository, times(1)).findByStatus(StatusAluno.ATIVO);
    }

    @Test
    @DisplayName("Deve buscar alunos por plano")
    void deveBuscarAlunosPorPlano() {
        List<Aluno> alunos = Arrays.asList(aluno);
        when(alunoRepository.findByPlanoId(1L)).thenReturn(alunos);

        List<Aluno> resultado = alunoService.findByPlano(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(alunoRepository, times(1)).findByPlanoId(1L);
    }

    @Test
    @DisplayName("Deve buscar alunos por instrutor")
    void deveBuscarAlunosPorInstrutor() {
        List<Aluno> alunos = Arrays.asList(aluno);
        when(alunoRepository.findByInstrutorId(1L)).thenReturn(alunos);

        List<Aluno> resultado = alunoService.findByInstrutor(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(alunoRepository, times(1)).findByInstrutorId(1L);
    }

    @Test
    @DisplayName("Deve calcular média de idade dos alunos")
    void deveCalcularMediaIdadeAlunos() {
        Double mediaEsperada = 30.5;
        when(alunoRepository.calcularMediaIdade()).thenReturn(mediaEsperada);

        Double resultado = alunoService.calcularMediaIdadeAlunos();

        assertNotNull(resultado);
        assertEquals(mediaEsperada, resultado);
        verify(alunoRepository, times(1)).calcularMediaIdade();
    }

    @Test
    @DisplayName("Deve contar alunos ativos")
    void deveContarAlunosAtivos() {
        Long totalEsperado = 10L;
        when(alunoRepository.countAlunosAtivos()).thenReturn(totalEsperado);

        Long resultado = alunoService.countAlunosAtivos();

        assertNotNull(resultado);
        assertEquals(totalEsperado, resultado);
        verify(alunoRepository, times(1)).countAlunosAtivos();
    }

    @Test
    @DisplayName("Deve buscar aluno por email")
    void deveBuscarAlunoPorEmail() {
        when(alunoRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(aluno));

        Optional<Aluno> resultado = alunoService.findByEmail("joao@email.com");

        assertTrue(resultado.isPresent());
        assertEquals("joao@email.com", resultado.get().getEmail());
        verify(alunoRepository, times(1)).findByEmail("joao@email.com");
    }

    @Test
    @DisplayName("Deve buscar aluno por CPF")
    void deveBuscarAlunoPorCpf() {
        when(alunoRepository.findByCpf("123.456.789-00")).thenReturn(Optional.of(aluno));

        Optional<Aluno> resultado = alunoService.findByCpf("123.456.789-00");

        assertTrue(resultado.isPresent());
        assertEquals("123.456.789-00", resultado.get().getCpf());
        verify(alunoRepository, times(1)).findByCpf("123.456.789-00");
    }
}

