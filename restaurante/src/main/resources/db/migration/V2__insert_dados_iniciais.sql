INSERT INTO mesas(numero, descricao, capacidade) VALUES
                                                     ( 1, 'Mesa próxima a entrada', 4 ),
(2, 'Mesa central', 4),
(3, 'Mesa próxima à janela', 2),
(4, 'Mesa família', 6),
(5, 'Mesa externa', 4);

INSERT INTO categorias_produtos(nome) VALUES
                                         ('Entradas'),
                                         ('Pratos principais'),
                                         ('Bebidas'),
                                         ('Sobremesas');

INSERT INTO produtos (categoria_id, nome, descricao, preco, tempo_preparo_minutos)
SELECT id, 'Batata frita', 'Portção de batata frita crocante', 28.90, 15
FROM categorias_produtos WHERE nome = 'Entradas';

INSERT INTO produtos (categoria_id, nome, descricao, preco, tempo_preparo_minutos)
SELECT id, 'X-Burguer artesanal', 'Hamburguer artesanal com queijo e molho especial', 34.90, 25
FROM categorias_produtos WHERE nome = 'Pratos Principais';

INSERT INTO produtos (categoria_id, nome, descricao, preco, tempo_preparo_minutos)
SELECT id, 'Filé com fritas', 'Filé grelhado acompanhado de batatas fritas', 59.90, 30
FROM categorias_produtos WHERE nome = 'Pratos Principais';

INSERT INTO produtos (categoria_id, nome, descricao, preco, tempo_preparo_minutos)
SELECT id, 'Suco natural', 'Suco natural da fruta', 12.00, 5
FROM categorias_produtos WHERE nome = 'Bebidas';

INSERT INTO produtos (categoria_id, nome, descricao, preco, tempo_preparo_minutos)
SELECT id, 'Pudim', 'Pudim tradicional', 14.90, 5
FROM categorias_produtos WHERE nome = 'Sobremesas';