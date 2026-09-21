export default function ProdutoList({ produtos, onEditar, onRemover }) {
  if (produtos.length === 0) {
    return (
      <p className="lista-vazia" data-testid="lista-vazia">
        Nenhum produto cadastrado ainda. Adicione o primeiro produto usando o formulário acima.
      </p>
    );
  }

  return (
    <table className="produto-tabela" data-testid="produto-tabela">
      <thead>
        <tr>
          <th>Nome</th>
          <th>Categoria</th>
          <th>Preço</th>
          <th>Quantidade</th>
          <th>Ações</th>
        </tr>
      </thead>
      <tbody>
        {produtos.map((produto) => (
          <tr key={produto.id} data-testid={`produto-linha-${produto.id}`}>
            <td>
              <strong>{produto.nome}</strong>
              {produto.descricao && <p className="descricao">{produto.descricao}</p>}
            </td>
            <td>{produto.categoria || '—'}</td>
            <td>
              {Number(produto.preco).toLocaleString('pt-BR', {
                style: 'currency',
                currency: 'BRL',
              })}
            </td>
            <td>{produto.quantidade}</td>
            <td className="acoes">
              <button onClick={() => onEditar(produto)} data-testid={`botao-editar-${produto.id}`}>
                Editar
              </button>
              <button
                onClick={() => onRemover(produto.id)}
                className="botao-remover"
                data-testid={`botao-remover-${produto.id}`}
              >
                Remover
              </button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}