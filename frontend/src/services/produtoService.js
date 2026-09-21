const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:3001/api';

async function tratarResposta(res) {
  if (!res.ok) {
    let mensagem = `Erro na requisição (status ${res.status})`;
    try {
      const dados = await res.json();
      if (dados.erro) mensagem = dados.erro;
    } catch {
      // resposta sem corpo JSON (ex: 204)
    }
    throw new Error(mensagem);
  }
  if (res.status === 204) return null;
  return res.json();
}

export async function listarProdutos() {
  const res = await fetch(`${API_URL}/produtos`);
  return tratarResposta(res);
}

export async function buscarProduto(id) {
  const res = await fetch(`${API_URL}/produtos/${id}`);
  return tratarResposta(res);
}

export async function criarProduto(produto) {
  const res = await fetch(`${API_URL}/produtos`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(produto),
  });
  return tratarResposta(res);
}

export async function atualizarProduto(id, produto) {
  const res = await fetch(`${API_URL}/produtos/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(produto),
  });
  return tratarResposta(res);
}

export async function removerProduto(id) {
  const res = await fetch(`${API_URL}/produtos/${id}`, {
    method: 'DELETE',
  });
  return tratarResposta(res);
}