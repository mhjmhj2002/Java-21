-- Desabilita as restrições de chave estrangeira para permitir a inserção em qualquer ordem (útil para H2)
SET REFERENTIAL_INTEGRITY FALSE;

-- Limpa as tabelas para garantir um estado inicial limpo a cada reinicialização
TRUNCATE TABLE item_pedido;
TRUNCATE TABLE pedido;
TRUNCATE TABLE produto;
TRUNCATE TABLE cliente;
TRUNCATE TABLE restaurante;

-- Habilita as restrições de chave estrangeira novamente
SET REFERENTIAL_INTEGRITY TRUE;

-- ===================================================================
-- CADASTRO DE CLIENTES E RESTAURANTES (sem alterações)
-- ===================================================================
INSERT INTO cliente (id, nome, email, telefone) VALUES
(1, 'Ana Silva', 'ana.silva@email.com', '11987654321'),
(2, 'Bruno Costa', 'bruno.costa@email.com', '21912345678');

INSERT INTO restaurante (id, nome, endereco) VALUES
(1, 'Pizzaria Firenza', 'Rua das Pizzas, 123, São Paulo'),
(2, 'Sushi House', 'Avenida do Peixe, 456, Rio de Janeiro');

-- ===================================================================
-- CADASTRO DE PRODUTOS (sem alterações)
-- ===================================================================
INSERT INTO produto (id, nome, preco, restaurante_id) VALUES
(101, 'Pizza Margherita', 45.00, 1),
(102, 'Pizza Calabresa', 50.00, 1),
(103, 'Refrigerante Lata', 8.00, 1);

INSERT INTO produto (id, nome, preco, restaurante_id) VALUES
(201, 'Combinado Salmão (20 peças)', 80.00, 2),
(202, 'Temaki Filadélfia', 35.00, 2),
(203, 'Água Mineral', 5.00, 2);


-- ===================================================================
-- CADASTRO DE PEDIDOS (AGORA COM 12 PEDIDOS)
-- ===================================================================
-- Pedidos Originais (garantindo um de cada status para o dashboard)
INSERT INTO pedido (id, cliente_id, restaurante_id, data_pedido, status, total) VALUES
(1, 1, 1, '2025-10-04 10:00:00', 'RECEBIDO', 61.00),
(2, 2, 2, '2025-10-04 11:30:00', 'CONFIRMADO', 80.00),
(3, 1, 2, '2025-10-04 12:00:00', 'EM_PREPARO', 70.00),
(4, 2, 1, '2025-10-04 12:15:00', 'SAIU_PARA_ENTREGA', 50.00),
(5, 1, 1, '2025-10-03 20:00:00', 'ENTREGUE', 45.00),
(6, 2, 2, '2025-10-03 21:00:00', 'CANCELADO', 35.00);

-- NOVOS PEDIDOS (para popular a paginação)
-- Pedido 7: Mais um pedido ENTREGUE
INSERT INTO pedido (id, cliente_id, restaurante_id, data_pedido, status, total) VALUES
(7, 2, 1, '2025-10-02 19:00:00', 'ENTREGUE', 58.00);

-- Pedido 8: Mais um pedido RECEBIDO
INSERT INTO pedido (id, cliente_id, restaurante_id, data_pedido, status, total) VALUES
(8, 1, 2, '2025-10-04 13:00:00', 'RECEBIDO', 40.00);

-- Pedido 9: Mais um pedido CONFIRMADO
INSERT INTO pedido (id, cliente_id, restaurante_id, data_pedido, status, total) VALUES
(9, 2, 1, '2025-10-04 13:10:00', 'CONFIRMADO', 45.00);

-- Pedido 10: Mais um pedido ENTREGUE
INSERT INTO pedido (id, cliente_id, restaurante_id, data_pedido, status, total) VALUES
(10, 1, 1, '2025-10-01 22:00:00', 'ENTREGUE', 50.00);

-- Pedido 11: Mais um pedido EM_PREPARO
INSERT INTO pedido (id, cliente_id, restaurante_id, data_pedido, status, total) VALUES
(11, 2, 2, '2025-10-04 13:20:00', 'EM_PREPARO', 85.00);

-- Pedido 12: Mais um pedido CANCELADO
INSERT INTO pedido (id, cliente_id, restaurante_id, data_pedido, status, total) VALUES
(12, 1, 2, '2025-10-02 14:00:00', 'CANCELADO', 35.00);


-- ===================================================================
-- CADASTRO DOS ITENS DE CADA PEDIDO
-- ===================================================================
-- Itens dos Pedidos Originais
INSERT INTO item_pedido (id, pedido_id, produto_id, quantidade, preco_unitario) VALUES
(1, 1, 101, 1, 45.00), (2, 1, 103, 2, 8.00),   -- Pedido 1
(3, 2, 201, 1, 80.00),                         -- Pedido 2
(4, 3, 202, 2, 35.00),                         -- Pedido 3
(5, 4, 102, 1, 50.00),                         -- Pedido 4
(6, 5, 101, 1, 45.00),                         -- Pedido 5
(7, 6, 202, 1, 35.00);                         -- Pedido 6

-- Itens dos NOVOS Pedidos
INSERT INTO item_pedido (id, pedido_id, produto_id, quantidade, preco_unitario) VALUES
(8, 7, 102, 1, 50.00), (9, 7, 103, 1, 8.00),    -- Pedido 7
(10, 8, 202, 1, 35.00), (11, 8, 203, 1, 5.00),  -- Pedido 8
(12, 9, 101, 1, 45.00),                        -- Pedido 9
(13, 10, 102, 1, 50.00),                       -- Pedido 10
(14, 11, 201, 1, 80.00), (15, 11, 203, 1, 5.00), -- Pedido 11
(16, 12, 202, 1, 35.00);                       -- Pedido 12

