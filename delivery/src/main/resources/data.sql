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
-- CADASTRO DE CLIENTES
-- ===================================================================
INSERT INTO cliente (id, nome, email, telefone) VALUES
(1, 'Ana Silva', 'ana.silva@email.com', '11987654321'),
(2, 'Bruno Costa', 'bruno.costa@email.com', '21912345678');

-- ===================================================================
-- CADASTRO DE RESTAURANTES
-- ===================================================================
INSERT INTO restaurante (id, nome, endereco) VALUES
(1, 'Pizzaria Firenza', 'Rua das Pizzas, 123, São Paulo'),
(2, 'Sushi House', 'Avenida do Peixe, 456, Rio de Janeiro');

-- ===================================================================
-- CADASTRO DE PRODUTOS
-- ===================================================================
-- Produtos da Pizzaria Firenza (restaurante_id = 1)
INSERT INTO produto (id, nome, preco, restaurante_id) VALUES
(101, 'Pizza Margherita', 45.00, 1),
(102, 'Pizza Calabresa', 50.00, 1),
(103, 'Refrigerante Lata', 8.00, 1);

-- Produtos do Sushi House (restaurante_id = 2)
INSERT INTO produto (id, nome, preco, restaurante_id) VALUES
(201, 'Combinado Salmão (20 peças)', 80.00, 2),
(202, 'Temaki Filadélfia', 35.00, 2),
(203, 'Água Mineral', 5.00, 2);


-- ===================================================================
-- CADASTRO DE PEDIDOS (UM PARA CADA STATUS)
-- ===================================================================
-- Pedido 1: RECEBIDO (Cliente Ana, Pizzaria Firenza)
-- Total: 1x Pizza Margherita (45.00) + 2x Refrigerante (16.00) = 61.00
INSERT INTO pedido (id, cliente_id, restaurante_id, data_pedido, status, total) VALUES
(1, 1, 1, '2025-10-04 10:00:00', 'RECEBIDO', 61.00);

-- Pedido 2: CONFIRMADO (Cliente Bruno, Sushi House)
-- Total: 1x Combinado Salmão (80.00) = 80.00
INSERT INTO pedido (id, cliente_id, restaurante_id, data_pedido, status, total) VALUES
(2, 2, 2, '2025-10-04 11:30:00', 'CONFIRMADO', 80.00);

-- Pedido 3: EM_PREPARO (Cliente Ana, Sushi House)
-- Total: 2x Temaki (70.00) = 70.00
INSERT INTO pedido (id, cliente_id, restaurante_id, data_pedido, status, total) VALUES
(3, 1, 2, '2025-10-04 12:00:00', 'EM_PREPARO', 70.00);

-- Pedido 4: SAIU_PARA_ENTREGA (Cliente Bruno, Pizzaria Firenza)
-- Total: 1x Pizza Calabresa (50.00) = 50.00
INSERT INTO pedido (id, cliente_id, restaurante_id, data_pedido, status, total) VALUES
(4, 2, 1, '2025-10-04 12:15:00', 'SAIU_PARA_ENTREGA', 50.00);

-- Pedido 5: ENTREGUE (Cliente Ana, Pizzaria Firenza)
-- Total: 1x Pizza Margherita (45.00) = 45.00
INSERT INTO pedido (id, cliente_id, restaurante_id, data_pedido, status, total) VALUES
(5, 1, 1, '2025-10-03 20:00:00', 'ENTREGUE', 45.00);

-- Pedido 6: CANCELADO (Cliente Bruno, Sushi House)
-- Total: 1x Temaki (35.00) = 35.00
INSERT INTO pedido (id, cliente_id, restaurante_id, data_pedido, status, total) VALUES
(6, 2, 2, '2025-10-03 21:00:00', 'CANCELADO', 35.00);


-- ===================================================================
-- CADASTRO DOS ITENS DE CADA PEDIDO
-- ===================================================================
-- Itens do Pedido 1 (RECEBIDO)
INSERT INTO item_pedido (id, pedido_id, produto_id, quantidade, preco_unitario) VALUES
(1, 1, 101, 1, 45.00), -- 1x Pizza Margherita
(2, 1, 103, 2, 8.00);  -- 2x Refrigerante

-- Itens do Pedido 2 (CONFIRMADO)
INSERT INTO item_pedido (id, pedido_id, produto_id, quantidade, preco_unitario) VALUES
(3, 2, 201, 1, 80.00); -- 1x Combinado Salmão

-- Itens do Pedido 3 (EM_PREPARO)
INSERT INTO item_pedido (id, pedido_id, produto_id, quantidade, preco_unitario) VALUES
(4, 3, 202, 2, 35.00); -- 2x Temaki

-- Itens do Pedido 4 (SAIU_PARA_ENTREGA)
INSERT INTO item_pedido (id, pedido_id, produto_id, quantidade, preco_unitario) VALUES
(5, 4, 102, 1, 50.00); -- 1x Pizza Calabresa

-- Itens do Pedido 5 (ENTREGUE)
INSERT INTO item_pedido (id, pedido_id, produto_id, quantidade, preco_unitario) VALUES
(6, 5, 101, 1, 45.00); -- 1x Pizza Margherita

-- Itens do Pedido 6 (CANCELADO)
INSERT INTO item_pedido (id, pedido_id, produto_id, quantidade, preco_unitario) VALUES
(7, 6, 202, 1, 35.00); -- 1x Temaki

