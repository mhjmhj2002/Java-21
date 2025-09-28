-- Inserir Restaurantes
INSERT INTO restaurante (nome, endereco) VALUES ('Pizzaria do Bairro', 'Rua das Pizzas, 123');
INSERT INTO restaurante (nome, endereco) VALUES ('Hamburgueria Central', 'Avenida Principal, 456');

-- Inserir Produtos para o Restaurante 1 (Pizzaria)
INSERT INTO produto (nome, descricao, preco, restaurante_id) VALUES ('Pizza Margherita', 'Molho de tomate, mussarela e manjericão', 45.00, 1);
INSERT INTO produto (nome, descricao, preco, restaurante_id) VALUES ('Pizza Calabresa', 'Molho de tomate, mussarela e calabresa fatiada', 50.00, 1);

-- Inserir Produtos para o Restaurante 2 (Hamburgueria)
INSERT INTO produto (nome, descricao, preco, restaurante_id) VALUES ('Cheeseburger Clássico', 'Pão, carne, queijo, alface e tomate', 30.00, 2);
INSERT INTO produto (nome, descricao, preco, restaurante_id) VALUES ('Burger Bacon', 'Pão, carne, queijo, bacon crocante e molho especial', 35.50, 2);

-- Inserir um Cliente
INSERT INTO cliente (nome, email, telefone) VALUES ('Ana Silva', 'ana.silva@email.com', '11987654321');

-- Inserir um Pedido para o Cliente 1
INSERT INTO pedido (cliente_id, restaurante_id, status, total, data_pedido) VALUES (1, 1, 'RECEBIDO', 95.00, CURRENT_TIMESTAMP);

-- Inserir Itens do Pedido 1
INSERT INTO item_pedido (pedido_id, produto_id, quantidade, preco_unitario) VALUES (1, 1, 1, 45.00);
INSERT INTO item_pedido (pedido_id, produto_id, quantidade, preco_unitario) VALUES (1, 2, 1, 50.00);
