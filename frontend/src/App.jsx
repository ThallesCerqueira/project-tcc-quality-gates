import { useState, useEffect, useCallback } from 'react';
import ProdutoForm from './components/ProdutoForm.jsx';
import ProdutoList from './components/ProdutoList.jsx';
import {
  listarProdutos,
  criarProduto,
  atualizarProduto,
  removerProduto,
} from './services/produtoService.js';

export default function App() {
  const [produtos, setProdutos] = useState([]);
  const [produtoEditando, setProdutoEditando] = useState(null);
  const [carregando, setCarregando] = useState(true);
  const [mensagemErro, setMensagemErro] = useState('');
  const [mensagemSucesso, setMensagemSucesso] = useState('');

  const carregarProdutos = useCallback(async () => {
    setCarregando(true);
    setMensagemErro('');
    try {
      const dados = await listarProdutos();
      setProdutos(dados);
    } catch (err) {
      setMensagemErro(err.message);
    } finally {
      setCarregando(false);
    }
  }, []);

  useEffect(() => {
    carregarProdutos();
  }, [carregarProdutos]);

  useEffect(() => {
    if (!mensagemSucesso) return;
    const timer = setTimeout(() => setMensagemSucesso(''), 3000);
    return () => clearTimeout(timer);
  }, [mensagemSucesso]);

  async function handleSalvar(dadosProduto) {
    setMensagemErro('');
    try {
      if (produtoEditando) {
        await atualizarProduto(produtoEditando.id, dadosProduto);
        setMensagemSucesso('Produto atualizado com sucesso.');
      } else {
        await criarProduto(dadosProduto);
        setMensagemSucesso('Produto adicionado com sucesso.');
      }
      setProdutoEditando(null);
      await carregarProdutos();
    } catch (err) {
      setMensagemErro(err.message);
    }
  }

  function handleEditar(produto) {
    setProdutoEditando(produto);
    setMensagemErro('');
  }

  function handleCancelarEdicao() {
    setProdutoEditando(null);
  }

  async function handleRemover(id) {
    const confirmar = window.confirm('Tem certeza que deseja remover este produto?');
    if (!confirmar) return;

    setMensagemErro('');
    try {
      await removerProduto(id);
      setMensagemSucesso('Produto removido com sucesso.');
      if (produtoEditando?.id === id) setProdutoEditando(null);
      await carregarProdutos();
    } catch (err) {
      setMensagemErro(err.message);
    }
  }

  return (
    <div className="container">
      <header className="cabecalho">
        <h1>Catálogo de Produtos</h1>
        <p>Gerencie o estoque de produtos do e-commerce.</p>
      </header>

      {mensagemErro && (
        <p className="mensagem-erro" data-testid="mensagem-erro" role="alert">
          {mensagemErro}
        </p>
      )}
      {mensagemSucesso && (
        <p className="mensagem-sucesso" data-testid="mensagem-sucesso" role="status">
          {mensagemSucesso}
        </p>
      )}

      <ProdutoForm
        produtoEditando={produtoEditando}
        onSalvar={handleSalvar}
        onCancelar={handleCancelarEdicao}
      />

      <section className="lista-secao">
        <h2>Produtos cadastrados</h2>
        {carregando ? (
          <p data-testid="carregando">Carregando produtos...</p>
        ) : (
          <ProdutoList produtos={produtos} onEditar={handleEditar} onRemover={handleRemover} />
        )}
      </section>
    </div>
  );
}