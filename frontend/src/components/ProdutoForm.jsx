import { useState, useEffect } from 'react';

const CAMPOS_INICIAIS = {
  nome: '',
  descricao: '',
  preco: '',
  quantidade: '',
  categoria: '',
};

export default function ProdutoForm({ produtoEditando, onSalvar, onCancelar }) {
  const [campos, setCampos] = useState(CAMPOS_INICIAIS);
  const [erro, setErro] = useState('');

  useEffect(() => {
    if (produtoEditando) {
      setCampos({
        nome: produtoEditando.nome || '',
        descricao: produtoEditando.descricao || '',
        preco: produtoEditando.preco ?? '',
        quantidade: produtoEditando.quantidade ?? '',
        categoria: produtoEditando.categoria || '',
      });
    } else {
      setCampos(CAMPOS_INICIAIS);
    }
    setErro('');
  }, [produtoEditando]);

  function handleChange(e) {
    const { name, value } = e.target;
    setCampos((prev) => ({ ...prev, [name]: value }));
  }

  function handleSubmit(e) {
    e.preventDefault();
    setErro('');

    if (!campos.nome.trim()) {
      setErro('O nome é obrigatório.');
      return;
    }

    const preco = parseFloat(campos.preco);
    if (Number.isNaN(preco) || preco < 0) {
      setErro('O preço deve ser um número válido maior ou igual a 0.');
      return;
    }

    const quantidade = campos.quantidade === '' ? 0 : parseInt(campos.quantidade, 10);
    if (Number.isNaN(quantidade) || quantidade < 0) {
      setErro('A quantidade deve ser um número inteiro maior ou igual a 0.');
      return;
    }

    onSalvar({
      nome: campos.nome.trim(),
      descricao: campos.descricao.trim(),
      preco,
      quantidade,
      categoria: campos.categoria.trim(),
    });
  }

  return (
    <form className="produto-form" onSubmit={handleSubmit} data-testid="produto-form">
      <h2>{produtoEditando ? 'Editar produto' : 'Novo produto'}</h2>

      {erro && (
        <p className="mensagem-erro" data-testid="form-erro" role="alert">
          {erro}
        </p>
      )}

      <div className="campo">
        <label htmlFor="nome">Nome *</label>
        <input
          id="nome"
          name="nome"
          type="text"
          value={campos.nome}
          onChange={handleChange}
          data-testid="input-nome"
        />
      </div>

      <div className="campo">
        <label htmlFor="descricao">Descrição</label>
        <textarea
          id="descricao"
          name="descricao"
          value={campos.descricao}
          onChange={handleChange}
          data-testid="input-descricao"
        />
      </div>

      <div className="campo-linha">
        <div className="campo">
          <label htmlFor="preco">Preço (R$) *</label>
          <input
            id="preco"
            name="preco"
            type="number"
            step="0.01"
            min="0"
            value={campos.preco}
            onChange={handleChange}
            data-testid="input-preco"
          />
        </div>

        <div className="campo">
          <label htmlFor="quantidade">Quantidade</label>
          <input
            id="quantidade"
            name="quantidade"
            type="number"
            min="0"
            value={campos.quantidade}
            onChange={handleChange}
            data-testid="input-quantidade"
          />
        </div>
      </div>

      <div className="campo">
        <label htmlFor="categoria">Categoria</label>
        <input
          id="categoria"
          name="categoria"
          type="text"
          value={campos.categoria}
          onChange={handleChange}
          data-testid="input-categoria"
        />
      </div>

      <div className="form-acoes">
        <button type="submit" data-testid="botao-salvar">
          {produtoEditando ? 'Salvar alterações' : 'Adicionar produto'}
        </button>
        {produtoEditando && (
          <button type="button" onClick={onCancelar} data-testid="botao-cancelar">
            Cancelar
          </button>
        )}
      </div>
    </form>
  );
}