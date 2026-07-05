const API_BASE = 'http://localhost:8080/api';

// Elementos do DOM
let alunos = [];
let planos = [];
let instrutores = [];

// Inicialização
document.addEventListener('DOMContentLoaded', function() {
    carregarDadosIniciais();
    configurarEventListeners();
});

function carregarDadosIniciais() {
    carregarAlunos();
    carregarPlanos();
    carregarInstrutores();
}

function configurarEventListeners() {
    // Formulário de aluno
    document.getElementById('formAluno').addEventListener('submit', cadastrarAluno);
    document.getElementById('formPlano').addEventListener('submit', cadastrarPlano);
    document.getElementById('formInstrutor').addEventListener('submit', cadastrarInstrutor);
}

// Funções de Tab
function openTab(tabName) {
    // Esconde todas as tabs
    const tabContents = document.getElementsByClassName('tab-content');
    for (let i = 0; i < tabContents.length; i++) {
        tabContents[i].classList.remove('active');
    }

    // Remove active de todos os botões
    const tabButtons = document.getElementsByClassName('tab-button');
    for (let i = 0; i < tabButtons.length; i++) {
        tabButtons[i].classList.remove('active');
    }

    // Mostra a tab selecionada
    document.getElementById(tabName).classList.add('active');
    event.currentTarget.classList.add('active');
}

// ALUNOS
async function carregarAlunos() {
    try {
        const response = await fetch(`${API_BASE}/alunos`);
        alunos = await response.json();
        exibirAlunos();
    } catch (error) {
        console.error('Erro ao carregar alunos:', error);
        mostrarErro('Erro ao carregar alunos');
    }
}

function exibirAlunos() {
    const lista = document.getElementById('listaAlunos');
    lista.innerHTML = '';

    if (alunos.length === 0) {
        lista.innerHTML = '<div class="loading">Nenhum aluno cadastrado</div>';
        return;
    }

    alunos.forEach(aluno => {
        const item = document.createElement('div');
        item.className = 'item-lista';
        item.innerHTML = `
            <div class="info-aluno">
                <h3>${aluno.nome}</h3>
                <p>📧 ${aluno.email}</p>
                <p>📞 ${aluno.telefone}</p>
                <p>🎂 ${new Date(aluno.dataNascimento).toLocaleDateString('pt-BR')}</p>
                <p>📋 Plano: ${aluno.plano?.nome || 'N/A'}</p>
                <span class="status-${aluno.ativo ? 'ativo' : 'inativo'}">
                    ${aluno.status || 'ATIVO'}
                </span>
            </div>
            <div class="acoes">
                <button class="btn-secondary" onclick="verDetalhesAluno(${aluno.id})">
                    Ver Detalhes
                </button>
                <button class="btn-danger" onclick="deletarAluno(${aluno.id})">
                    Excluir
                </button>
            </div>
        `;
        lista.appendChild(item);
    });
}

async function cadastrarAluno(event) {
    event.preventDefault();

    const aluno = {
        nome: document.getElementById('nome').value,
        email: document.getElementById('email').value,
        cpf: document.getElementById('cpf').value,
        dataNascimento: document.getElementById('dataNascimento').value,
        telefone: document.getElementById('telefone').value,
        endereco: document.getElementById('endereco').value,
        plano: { id: parseInt(document.getElementById('planoId').value) },
        instrutor: document.getElementById('instrutorId').value ?
            { id: parseInt(document.getElementById('instrutorId').value) } : null
    };

    try {
        const response = await fetch(`${API_BASE}/alunos`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(aluno)
        });

        if (response.ok) {
            document.getElementById('formAluno').reset();
            carregarAlunos();
            mostrarSucesso('Aluno cadastrado com sucesso!');
        } else {
            const error = await response.json();
            mostrarErro(error.message || 'Erro ao cadastrar aluno');
        }
    } catch (error) {
        console.error('Erro:', error);
        mostrarErro('Erro ao conectar com o servidor');
    }
}

// PLANOS
async function carregarPlanos() {
    try {
        const response = await fetch(`${API_BASE}/planos`);
        planos = await response.json();
        exibirPlanos();
        preencherSelectPlanos();
    } catch (error) {
        console.error('Erro ao carregar planos:', error);
    }
}

function exibirPlanos() {
    const lista = document.getElementById('listaPlanos');
    lista.innerHTML = '';

    planos.forEach(plano => {
        const item = document.createElement('div');
        item.className = 'item-lista';
        item.innerHTML = `
            <div class="info-aluno">
                <h3>${plano.nome}</h3>
                <p>${plano.descricao}</p>
                <p>💰 R$ ${plano.valorMensal.toFixed(2)}/mês</p>
                <p>⏱️ ${plano.duracaoMeses} meses</p>
                <p>🏋️ ${plano.aulasPorSemana || '0'} aulas/semana</p>
                <span class="status-${plano.ativo ? 'ativo' : 'inativo'}">
                    ${plano.ativo ? 'ATIVO' : 'INATIVO'}
                </span>
            </div>
        `;
        lista.appendChild(item);
    });
}

function preencherSelectPlanos() {
    const select = document.getElementById('planoId');
    select.innerHTML = '<option value="">Selecione o plano...</option>';

    planos.filter(p => p.ativo).forEach(plano => {
        const option = document.createElement('option');
        option.value = plano.id;
        option.textContent = `${plano.nome} - R$ ${plano.valorMensal.toFixed(2)}`;
        select.appendChild(option);
    });
}

async function cadastrarPlano(event) {
    event.preventDefault();

    const plano = {
        nome: document.getElementById('nomePlano').value,
        descricao: document.getElementById('descricaoPlano').value,
        valorMensal: parseFloat(document.getElementById('valorMensal').value),
        duracaoMeses: parseInt(document.getElementById('duracaoMeses').value),
        aulasPorSemana: parseInt(document.getElementById('aulasPorSemana').value) || null,
        tipo: document.getElementById('tipoPlano').value,
        ativo: true
    };

    try {
        const response = await fetch(`${API_BASE}/planos`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(plano)
        });

        if (response.ok) {
            document.getElementById('formPlano').reset();
            carregarPlanos();
            mostrarSucesso('Plano cadastrado com sucesso!');
        } else {
            const error = await response.json();
            mostrarErro(error.message || 'Erro ao cadastrar plano');
        }
    } catch (error) {
        console.error('Erro:', error);
        mostrarErro('Erro ao conectar com o servidor');
    }
}

// INSTRUTORES
async function carregarInstrutores() {
    try {
        const response = await fetch(`${API_BASE}/instrutores`);
        instrutores = await response.json();
        exibirInstrutores();
        preencherSelectInstrutores();
    } catch (error) {
        console.error('Erro ao carregar instrutores:', error);
    }
}

function exibirInstrutores() {
    const lista = document.getElementById('listaInstrutores');
    lista.innerHTML = '';

    instrutores.forEach(instrutor => {
        const item = document.createElement('div');
        item.className = 'item-lista';
        item.innerHTML = `
            <div class="info-aluno">
                <h3>${instrutor.nome}</h3>
                <p>📧 ${instrutor.email}</p>
                <p>🎯 ${instrutor.especialidade}</p>
                <p>💰 R$ ${instrutor.salario?.toFixed(2) || 'N/A'}</p>
                <span class="status-${instrutor.status === 'ATIVO' ? 'ativo' : 'inativo'}">
                    ${instrutor.status}
                </span>
            </div>
        `;
        lista.appendChild(item);
    });
}

function preencherSelectInstrutores() {
    const select = document.getElementById('instrutorId');
    select.innerHTML = '<option value="">Selecione o instrutor (opcional)</option>';

    instrutores.filter(i => i.status === 'ATIVO').forEach(instrutor => {
        const option = document.createElement('option');
        option.value = instrutor.id;
        option.textContent = `${instrutor.nome} - ${instrutor.especialidade}`;
        select.appendChild(option);
    });
}

async function cadastrarInstrutor(event) {
    event.preventDefault();

    const instrutor = {
        nome: document.getElementById('nomeInstrutor').value,
        email: document.getElementById('emailInstrutor').value,
        cpf: document.getElementById('cpfInstrutor').value,
        dataNascimento: document.getElementById('dataNascimentoInstrutor').value,
        telefone: document.getElementById('telefoneInstrutor').value,
        especialidade: document.getElementById('especialidade').value,
        salario: document.getElementById('salario').value ?
            parseFloat(document.getElementById('salario').value) : null,
        numeroRegistro: document.getElementById('numeroRegistro').value || null,
        status: 'ATIVO'
    };

    try {
        const response = await fetch(`${API_BASE}/instrutores`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(instrutor)
        });

        if (response.ok) {
            document.getElementById('formInstrutor').reset();
            carregarInstrutores();
            mostrarSucesso('Instrutor cadastrado com sucesso!');
        } else {
            const error = await response.json();
            mostrarErro(error.message || 'Erro ao cadastrar instrutor');
        }
    } catch (error) {
        console.error('Erro:', error);
        mostrarErro('Erro ao conectar com o servidor');
    }
}

// FUNÇÕES UTILITÁRIAS
async function verDetalhesAluno(id) {
    try {
        const response = await fetch(`${API_BASE}/alunos/${id}`);
        const aluno = await response.json();

        const modalBody = document.getElementById('modalBody');
        modalBody.innerHTML = `
            <h2>Detalhes do Aluno</h2>
            <div style="margin-top: 20px;">
                <p><strong>Nome:</strong> ${aluno.nome}</p>
                <p><strong>Email:</strong> ${aluno.email}</p>
                <p><strong>CPF:</strong> ${aluno.cpf}</p>
                <p><strong>Data Nascimento:</strong> ${new Date(aluno.dataNascimento).toLocaleDateString('pt-BR')}</p>
                <p><strong>Telefone:</strong> ${aluno.telefone}</p>
                <p><strong>Endereço:</strong> ${aluno.endereco || 'Não informado'}</p>
                <p><strong>Plano:</strong> ${aluno.plano?.nome || 'N/A'}</p>
                <p><strong>Instrutor:</strong> ${aluno.instrutor?.nome || 'Não atribuído'}</p>
                <p><strong>Status:</strong> ${aluno.status}</p>
                <p><strong>Data Matrícula:</strong> ${new Date(aluno.dataMatricula).toLocaleDateString('pt-BR')}</p>
            </div>
        `;

        document.getElementById('modal').style.display = 'block';
    } catch (error) {
        console.error('Erro:', error);
        mostrarErro('Erro ao carregar detalhes do aluno');
    }
}

async function deletarAluno(id) {
    if (confirm('Tem certeza que deseja excluir este aluno?')) {
        try {
            const response = await fetch(`${API_BASE}/alunos/${id}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                carregarAlunos();
                mostrarSucesso('Aluno excluído com sucesso!');
            } else {
                mostrarErro('Erro ao excluir aluno');
            }
        } catch (error) {
            console.error('Erro:', error);
            mostrarErro('Erro ao conectar com o servidor');
        }
    }
}

function filtrarAlunos() {
    const termo = document.getElementById('searchAluno').value.toLowerCase();
    const alunosFiltrados = alunos.filter(aluno =>
        aluno.nome.toLowerCase().includes(termo) ||
        aluno.email.toLowerCase().includes(termo) ||
        aluno.cpf.includes(termo)
    );

    const lista = document.getElementById('listaAlunos');
    lista.innerHTML = '';

    alunosFiltrados.forEach(aluno => {
        const item = document.createElement('div');
        item.className = 'item-lista';
        item.innerHTML = `
            <div class="info-aluno">
                <h3>${aluno.nome}</h3>
                <p>📧 ${aluno.email}</p>
                <p>📞 ${aluno.telefone}</p>
                <span class="status-${aluno.ativo ? 'ativo' : 'inativo'}">
                    ${aluno.status || 'ATIVO'}
                </span>
            </div>
            <div class="acoes">
                <button class="btn-secondary" onclick="verDetalhesAluno(${aluno.id})">
                    Ver Detalhes
                </button>
                <button class="btn-danger" onclick="deletarAluno(${aluno.id})">
                    Excluir
                </button>
            </div>
        `;
        lista.appendChild(item);
    });
}

function fecharModal() {
    document.getElementById('modal').style.display = 'none';
}

function mostrarErro(mensagem) {
    // Implementação simples de mensagem de erro
    alert('Erro: ' + mensagem);
}

function mostrarSucesso(mensagem) {
    // Implementação simples de mensagem de sucesso
    alert('Sucesso: ' + mensagem);
}

// Fecha modal clicando fora
window.onclick = function(event) {
    const modal = document.getElementById('modal');
    if (event.target === modal) {
        fecharModal();
    }
}