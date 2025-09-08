-- Inserir cliente inicial se a tabela estiver vazia
MERGE INTO clientes (id, nome, email, telefone) KEY(id) 
VALUES (1, 'João Silva', 'joao.silva@email.com', '(11) 99999-9999');

MERGE INTO clientes (id, nome, email, telefone) KEY(id) 
VALUES (2, 'Maria Santos', 'maria.santos@email.com', '(11) 98888-8888');

MERGE INTO clientes (id, nome, email, telefone) KEY(id) 
VALUES (3, 'Pedro Oliveira', 'pedro.oliveira@email.com', '(11) 97777-7777');

MERGE INTO clientes (id, nome, email, telefone) KEY(id) 
VALUES (4, 'Ana Costa', 'ana.costa@email.com', '(11) 96666-6666');

MERGE INTO clientes (id, nome, email, telefone) KEY(id) 
VALUES (5, 'Carlos Pereira', 'carlos.pereira@email.com', '(11) 95555-5555');