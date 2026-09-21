const pool = require('../db/pool');

// Listar todos os produtos
async function listarProdutos(req, res) {
  try {
    const result = await pool.query('SELECT * FROM produtos ORDER BY id ASC');
    res.json(result.rows);
  } catch (err) {
    console.error(err);
    res.status(500).json({ erro: 'Erro ao buscar produtos' });
  }
}

// Buscar produto por ID
async function buscarProdutoPorId(req, res) {
  const { id } = req.params;
  try {
    const result = await pool.query('SELECT * FROM produtos WHERE id = $1', [id]);
    if (result.rows.length === 0) {
      return res.status(404).json({ erro: 'Produto não encontrado' });
    }
    res.json(result.rows[0]);
  } catch (err) {
    console.error(err);
    res.status(500).json({ erro: 'Erro ao buscar produto' });
  }
}

// Criar novo produto
async function criarProduto(req, res) {
  const { nome, descricao, preco, quantidade, categoria } = req.body;

  if (!nome || preco === undefined) {
    return res.status(400).json({ erro: 'Os campos "nome" e "preco" são obrigatórios' });
  }

  if (typeof preco !== 'number' || preco < 0) {
    return res.status(400).json({ erro: 'O campo "preco" deve ser um número maior ou igual a 0' });
  }

  if (quantidade !== undefined && (typeof quantidade !== 'number' || quantidade < 0)) {
    return res.status(400).json({ erro: 'O campo "quantidade" deve ser um número maior ou igual a 0' });
  }

  try {
    const result = await pool.query(
      `INSERT INTO produtos (nome, descricao, preco, quantidade, categoria)
       VALUES ($1, $2, $3, $4, $5) RETURNING *`,
      [nome, descricao || null, preco, quantidade || 0, categoria || null]
    );
    res.status(201).json(result.rows[0]);
  } catch (err) {
    console.error(err);
    res.status(500).json({ erro: 'Erro ao criar produto' });
  }
}

// Atualizar produto existente
async function atualizarProduto(req, res) {
  const { id } = req.params;
  const { nome, descricao, preco, quantidade, categoria } = req.body;

  if (!nome || preco === undefined) {
    return res.status(400).json({ erro: 'Os campos "nome" e "preco" são obrigatórios' });
  }

  if (typeof preco !== 'number' || preco < 0) {
    return res.status(400).json({ erro: 'O campo "preco" deve ser um número maior ou igual a 0' });
  }

  if (quantidade !== undefined && (typeof quantidade !== 'number' || quantidade < 0)) {
    return res.status(400).json({ erro: 'O campo "quantidade" deve ser um número maior ou igual a 0' });
  }

  try {
    const result = await pool.query(
      `UPDATE produtos
       SET nome = $1, descricao = $2, preco = $3, quantidade = $4, categoria = $5, atualizado_em = NOW()
       WHERE id = $6 RETURNING *`,
      [nome, descricao || null, preco, quantidade || 0, categoria || null, id]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ erro: 'Produto não encontrado' });
    }

    res.json(result.rows[0]);
  } catch (err) {
    console.error(err);
    res.status(500).json({ erro: 'Erro ao atualizar produto' });
  }
}

// Remover produto
async function removerProduto(req, res) {
  const { id } = req.params;
  try {
    const result = await pool.query('DELETE FROM produtos WHERE id = $1 RETURNING *', [id]);

    if (result.rows.length === 0) {
      return res.status(404).json({ erro: 'Produto não encontrado' });
    }

    res.status(204).send();
  } catch (err) {
    console.error(err);
    res.status(500).json({ erro: 'Erro ao remover produto' });
  }
}

module.exports = {
  listarProdutos,
  buscarProdutoPorId,
  criarProduto,
  atualizarProduto,
  removerProduto,
};