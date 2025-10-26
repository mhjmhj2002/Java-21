-- Operadores Logísticos
INSERT INTO operador_logistico (id, nome) VALUES (1, 'Correios'), (2, 'FedEx'), (3, 'Loggi');

-- Faixas de CEP de Exemplo
-- RJ (Correios)
INSERT INTO faixa_cep (id, cep_inicial, cep_final, cidade, uf, tipo_entrega, operador_logistico_id) VALUES
(101, '20000000', '23799999', 'Rio de Janeiro', 'RJ', 'Normal', 1),
(102, '20000000', '23799999', 'Rio de Janeiro', 'RJ', 'Expresso', 2); -- FedEx faz o expresso

-- SP (Loggi)
INSERT INTO faixa_cep (id, cep_inicial, cep_final, cidade, uf, tipo_entrega, operador_logistico_id) VALUES
(103, '01000000', '05899999', 'São Paulo', 'SP', 'Normal', 3);
