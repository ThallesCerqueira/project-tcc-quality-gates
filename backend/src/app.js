const express = require('express');
const cors = require('cors');
const produtoRoutes = require('./routes/produtoRoutes');

const app = express();

app.use(cors());
app.use(express.json());

// Rota de health check (útil para CI/CD e monitoramento) - modificacao
app.get('/health', (req, res) => {
  res.json({ status: 'ok' });
});

app.use('/api', produtoRoutes);

// Tratamento de rota não encontrada
app.use((req, res) => {
  res.status(404).json({ erro: 'Rota não encontrada' });
});

module.exports = app;