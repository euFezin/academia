-- ######################################################################
-- 1. ENTIDADES BASE (COM IDS EXPLÍCITOS PARA GARANTIR INTEGRIDADE REFERENCIAL)
-- ######################################################################

-- INSERT INSTRUTORES (ID 101, 102)
INSERT INTO instrutores (id, nome, email, cpf, data_nascimento, telefone, especialidade, status)
VALUES (101, 'Ana Silva', 'ana@academia.com', '11111111111', '1985-06-15', '999998888', 'Musculação Avançada', 'ATIVO');
INSERT INTO instrutores (id, nome, email, cpf, data_nascimento, telefone, especialidade, status)
VALUES (102, 'Bruno Costa', 'bruno@academia.com', '22222222222', '1990-11-25', '988887777', 'Pilates e Yoga', 'ATIVO');

-- INSERT PLANOS (ID 1, 2, 3)
INSERT INTO planos (id, nome, descricao, valor_mensal, duracao_meses, tipo, ativo, aulas_semana)
VALUES (1, 'Plano Básico', 'Acesso limitado', 69.90, 1, 'BASICO', TRUE, 3);
INSERT INTO planos (id, nome, descricao, valor_mensal, duracao_meses, tipo, ativo, aulas_semana)
VALUES (2, 'Plano Premium', 'Acesso total + Aulas', 129.90, 12, 'PREMIUM', TRUE, 7);
INSERT INTO planos (id, nome, descricao, valor_mensal, duracao_meses, tipo, ativo, aulas_semana)
VALUES (3, 'Plano Diário', 'Acesso avulso por um dia', 10.00, 0, 'BASICO', FALSE, 1);

-- INSERT EXERCÍCIOS (ID 401, 402, 403)
INSERT INTO exercicios (id, nome, grupo_muscular, instrucoes)
VALUES (401, 'Agachamento Livre', 'Pernas', 'Desça até a paralela.');
INSERT INTO exercicios (id, nome, grupo_muscular, instrucoes)
VALUES (402, 'Supino Reto', 'Peito', 'Abaixe a barra até o peito.');
INSERT INTO exercicios (id, nome, grupo_muscular, instrucoes)
VALUES (403, 'Desenvolvimento', 'Ombros', 'Eleve a barra acima da cabeça.');

-- INSERT TURMAS (ID 301, 302)
INSERT INTO turmas (id, nome, descricao, capacidade_maxima, nivel, ativo, idade_minima)
VALUES (301, 'Yoga Matinal', 'Relaxamento e flexibilidade', 15, 'INICIANTE', TRUE, 18);
INSERT INTO turmas (id, nome, descricao, capacidade_maxima, nivel, ativo, idade_minima)
VALUES (302, 'HIIT Avançado', 'Treino intervalado de alta intensidade', 10, 'AVANCADO', TRUE, 20);

-- ######################################################################
-- 2. ENTIDADES DEPENDENTES (COM FK)
-- ######################################################################

-- INSERT ALUNOS (ID 201, 202, 203 - Depende de Plano e Instrutor)
INSERT INTO alunos (id, nome, email, cpf, data_nascimento, telefone, endereco, status, plano_id, instrutor_id, data_matricula)
VALUES (201, 'Carlos Mendes', 'carlos@email.com', '33333333333', '1995-05-10', '988887777', 'Rua A', 'ATIVO', 2, 101, '2025-10-01 10:00:00');
INSERT INTO alunos (id, nome, email, cpf, data_nascimento, telefone, endereco, status, plano_id, instrutor_id, data_matricula)
VALUES (202, 'Diana Souza', 'diana@email.com', '44444444444', '2000-01-20', '977776666', 'Rua B', 'ATIVO', 1, 101, '2025-10-01 10:00:00');
INSERT INTO alunos (id, nome, email, cpf, data_nascimento, telefone, endereco, status, plano_id, instrutor_id, data_matricula)
VALUES (203, 'Elena Faro', 'elena@email.com', '55555555555', '1998-03-01', '966665555', 'Rua C', 'INATIVO', 2, 102, '2025-10-01 10:00:00');


-- ######################################################################
-- 3. ENTIDADES DE JUNÇÃO E CHAVES COMPOSTAS (USANDO IDS FIXOS)
-- ######################################################################

-- TREINOS (ID 501, 502 - Usaremos esses IDs para garantir a FK)
INSERT INTO treinos (id, aluno_id, instrutor_id, data_criacao, data_validade, nome, tipo, ativo, descricao)
VALUES (501, 201, 101, '2025-11-01', '2025-12-01', 'Treino Força A', 'MUSCULACAO', TRUE, 'Foco em membros superiores e inferiores.');
INSERT INTO treinos (id, aluno_id, instrutor_id, data_criacao, data_validade, nome, tipo, ativo)
VALUES (502, 201, 102, '2025-10-01', '2025-10-30', 'Treino Cardio', 'CARDIO', FALSE);

-- EXERCÍCIO TREINO (Referências corrigidas para ID 501 e 502)
INSERT INTO exercicios_treino (treino_id, exercicio_id, series, repeticoes, carga_estimada)
VALUES (501, 401, 4, 12, 80.0);
INSERT INTO exercicios_treino (treino_id, exercicio_id, series, repeticoes, carga_estimada)
VALUES (501, 402, 3, 10, 50.0);
INSERT INTO exercicios_treino (treino_id, exercicio_id, series, repeticoes, carga_estimada)
VALUES (502, 403, 3, 15, 20.0);


-- RELAÇÃO N:N (Aluno_Turma - ID 201, 301, etc.)
INSERT INTO aluno_turma (aluno_id, turma_id)
VALUES (201, 301);
INSERT INTO aluno_turma (aluno_id, turma_id)
VALUES (201, 302);
INSERT INTO aluno_turma (aluno_id, turma_id)
VALUES (202, 301);

-- AVALIAÇÃO FÍSICA (Chave Composta/FK como PK: aluno_id, data_avaliacao - ID 201, 101)
INSERT INTO avaliacoes_fisicas (aluno_id, data_avaliacao, instrutor_id, peso, altura, percentual_gordura, massa_muscular)
VALUES (201, '2025-10-01', 101, 80.5, 1.80, 18.5, 40.0);
INSERT INTO avaliacoes_fisicas (aluno_id, data_avaliacao, instrutor_id, peso, altura, percentual_gordura, massa_muscular)
VALUES (201, '2025-11-01', 101, 79.8, 1.80, 17.9, 41.2);

-- PAGAMENTOS (ID 601, 602 - Depende de Aluno e Plano)
INSERT INTO pagamentos (id, aluno_id, plano_id, data_vencimento, data_pagamento, valor, status, metodo)
VALUES (601, 201, 2, '2025-10-10', '2025-10-09', 129.90, 'PAGO', 'CARTAO_CREDITO');
INSERT INTO pagamentos (id, aluno_id, plano_id, data_vencimento, data_pagamento, valor, status, metodo)
VALUES (602, 201, 2, '2025-10-25', '1900-01-01', 129.90, 'PENDENTE', 'CARTAO_DEBITO');
INSERT INTO pagamentos (id, aluno_id, plano_id, data_vencimento, data_pagamento, valor, status, metodo)
VALUES (603, 202, 1, '2025-11-15', '2025-11-15', 69.90, 'PAGO', 'PIX');

-- Aulas (ID 701, 702, 703)
INSERT INTO aulas (id, data_hora, duracao_minutos, instrutor_id, turma_id, ativa, observacoes)
VALUES (701, '2025-11-10 08:00:00', 60, 102, 301, TRUE, 'Aula de Yoga Matinal com Instrutor Bruno');
INSERT INTO aulas (id, data_hora, duracao_minutos, instrutor_id, turma_id, ativa, observacoes)
VALUES (702, '2025-11-12 08:00:00', 60, 102, 301, TRUE, 'Aula de Yoga Matinal com Instrutor Bruno');
INSERT INTO aulas (id, data_hora, duracao_minutos, instrutor_id, turma_id, ativa, observacoes)
VALUES (703, '2025-11-11 18:00:00', 45, 101, 302, TRUE, 'HIIT Avançado com Instrutora Ana');

INSERT INTO aula_aluno (aula_id, aluno_id)
VALUES (701, 201);
INSERT INTO aula_aluno (aula_id, aluno_id)
VALUES (701, 202);
INSERT INTO aula_aluno (aula_id, aluno_id)
VALUES (703, 201);