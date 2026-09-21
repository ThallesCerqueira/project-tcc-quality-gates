-- Criação da tabela de produtos
CREATE TABLE IF NOT EXISTS produtos (
  id SERIAL PRIMARY KEY,
  nome VARCHAR(255) NOT NULL,
  descricao TEXT,
  preco NUMERIC(10, 2) NOT NULL CHECK (preco >= 0),
  quantidade INTEGER NOT NULL DEFAULT 0 CHECK (quantidade >= 0),
  categoria VARCHAR(100),
  criado_em TIMESTAMP DEFAULT NOW(),
  atualizado_em TIMESTAMP DEFAULT NOW()
);

-- Dados de exemplo (seed)
INSERT INTO produtos (nome, descricao, preco, quantidade, categoria) VALUES
  ('Notebook Gamer', 'Notebook com placa de vídeo dedicada, 16GB RAM', 4500.00, 10, 'Eletrônicos'),
  ('Mouse sem fio', 'Mouse ergonômico sem fio com bateria recarregável', 89.90, 50, 'Acessórios'),
  ('Teclado Mecânico', 'Teclado mecânico RGB switch azul', 250.00, 30, 'Acessórios'),
  ('Monitor 24"', 'Monitor Full HD 24 polegadas IPS', 750.00, 15, 'Eletrônicos'),
  ('Cadeira Gamer', 'Cadeira ergonômica reclinável', 999.00, 5, 'Mobiliário')
ON CONFLICT DO NOTHING;

-- sudo systemctl start postgresql
-- sudo systemctl stop postgresql