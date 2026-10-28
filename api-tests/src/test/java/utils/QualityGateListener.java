package utils;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QualityGateListener implements TestExecutionListener {

    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;
    private int abortedTests = 0;

    private int criticalTotal = 0;
    private int criticalPassed = 0;

    private final Map<String, Long> startTimes = new HashMap<>();
    private final List<TestResult> results = new ArrayList<>();

    private static class TestResult {

        String name;
        String status;
        long duration;

        TestResult(String name, String status, long duration) {
            this.name = name;
            this.status = status;
            this.duration = duration;
        }
    }

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {

        if (!testIdentifier.isTest()) {
            return;
        }

        startTimes.put(testIdentifier.getUniqueId(), System.currentTimeMillis());
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {

        if (!testIdentifier.isTest()) {
            return;
        }

        totalTests++;

        long start = startTimes.getOrDefault(testIdentifier.getUniqueId(), System.currentTimeMillis());

        long duration = System.currentTimeMillis() - start;

        String status =
                switch (testExecutionResult.getStatus()) {
                    case SUCCESSFUL -> "APROVADO";
                    case FAILED -> "REPROVADO";
                    case ABORTED -> "ABORTADO";
                };

        results.add(
                new TestResult(
                        testIdentifier.getDisplayName(),
                        status,
                        duration
                )
        );

        boolean critical = testIdentifier.getTags().stream().anyMatch(tag -> tag.getName().equalsIgnoreCase("critical"));

        if (critical) {
            criticalTotal++;
        }

        switch (testExecutionResult.getStatus()) {

            case SUCCESSFUL -> {
                passedTests++;

                if (critical) {
                    criticalPassed++;
                }
            }

            case FAILED -> failedTests++;

            case ABORTED -> abortedTests++;
        }
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {

        try {

            Path directory = Path.of("target");

            Files.createDirectories(directory);

            generateQualityGateProperties();

            generateHtmlReport();

        } catch (IOException e) {

            throw new RuntimeException(
                    "Erro ao gerar os dados da execução dos testes.",
                    e
            );
        }
    }

    private void generateQualityGateProperties()
            throws IOException {

        String result =
                "total_tests=" + totalTests + "\n" +
                        "passed_tests=" + passedTests + "\n" +
                        "failed_tests=" + failedTests + "\n" +
                        "aborted_tests=" + abortedTests + "\n" +
                        "critical_total=" + criticalTotal + "\n" +
                        "critical_passed=" + criticalPassed + "\n";

        Files.writeString(
                Path.of(
                        "target",
                        "quality-gate.properties"
                ),
                result
        );
    }

    private void generateHtmlReport() throws IOException {

        StringBuilder html = new StringBuilder();

        String dataExecucao = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        String versao = System.getenv()
                .getOrDefault("APP_VERSION", "Execução local");

        String branch = System.getenv()
                .getOrDefault("GITHUB_REF_NAME", "local");

        String commit = System.getenv()
                .getOrDefault("GITHUB_SHA", "local");

        if (!commit.equals("local") && commit.length() > 7) {
            commit = commit.substring(0, 7);
        }

        String pipeline = System.getenv()
                .getOrDefault("GITHUB_RUN_NUMBER", "local");

        String ambiente = System.getenv()
                .getOrDefault("BASE_URL", "http://localhost:3002");

        double percentualAprovacao = 0;

        if (totalTests > 0) {
            percentualAprovacao =
                    ((double) passedTests / totalTests) * 100;
        }

        long unexpected5xx = countUnexpected5xx();

        boolean criterioCriticos =
                criticalTotal > 0 &&
                        criticalPassed == criticalTotal;

        boolean criterioPercentual =
                percentualAprovacao >= 90;

        boolean criterio5xx =
                unexpected5xx == 0;

        boolean qualityGateAprovado =
                criterioCriticos &&
                        criterioPercentual &&
                        criterio5xx;

        html.append("""
                <!DOCTYPE html>
                <html lang="pt-BR">
                
                <head>
                
                    <meta charset="UTF-8">
                
                    <meta
                        name="viewport"
                        content="width=device-width, initial-scale=1.0"
                    >
                
                    <title>
                        Relatório de Execução dos Testes
                    </title>
                
                    <style>
                
                        body {
                            font-family: Arial, sans-serif;
                            margin: 40px;
                            background-color: #f8f9fa;
                            color: #212529;
                        }
                
                        h1 {
                            margin-bottom: 30px;
                        }
                
                        h2 {
                            margin-top: 30px;
                        }
                
                        table {
                            border-collapse: collapse;
                            width: 100%;
                            margin-bottom: 30px;
                            background-color: white;
                        }
                
                        th,
                        td {
                            border: 1px solid #fddd;
                            padding: 10px;
                            text-align: left;
                        }
                
                        th {
                            background-color: #f2f2f2;
                        }
                
                        .APROVADO {
                            color: green;
                            font-weight: bold;
                        }
                
                        .REPROVADO {
                            color: red;
                            font-weight: bold;
                        }
                
                        .ABORTADO {
                            color: orange;
                            font-weight: bold;
                        }
                
                        .resumo {
                            display: flex;
                            gap: 15px;
                            flex-wrap: wrap;
                            margin-bottom: 30px;
                        }
                
                        .card {
                            background-color: white;
                            border: 1px solid #ddd;
                            border-radius: 6px;
                            padding: 15px;
                            min-width: 160px;
                        }
                
                        .card strong {
                            display: block;
                            font-size: 22px;
                            margin-top: 5px;
                        }
                
                        .criterio-atendido {
                            color: green;
                            font-weight: bold;
                        }
                
                        .criterio-nao-atendido {
                            color: red;
                            font-weight: bold;
                        }
                
                        .gate-status {
                            background-color: white;
                            border: 1px solid #ddd;
                            border-left: 4px solid;
                            padding: 12px 15px;
                            margin-bottom: 30px;
                        }
                
                        .gate-aprovado {
                            border-left-color: green;
                            color: green;
                        }
                
                        .gate-reprovado {
                            border-left-color: red;
                            color: red;
                        }
                
                    </style>
                
                </head>
                
                <body>
                
                    <h1>
                        Relatório de Execução dos Testes
                    </h1>
                
                    <h2>
                        Identificação da execução
                    </h2>
                
                    <table>
                
                        <tr>
                            <th>Informação</th>
                            <th>Valor</th>
                        </tr>
                """);

        appendRow(html, "Data/Hora", dataExecucao);
        appendRow(html, "Versão", versao);
        appendRow(html, "Branch", branch);
        appendRow(html, "Commit", commit);
        appendRow(html, "Execução do pipeline", "#" + pipeline);
        appendRow(html, "Ambiente", ambiente);

        html.append("""
                    </table>
                
                    <h2>
                        Resumo da execução
                    </h2>
                
                    <div class="resumo">
                """);

        appendCard(
                html,
                "Total",
                String.valueOf(totalTests)
        );

        appendCard(
                html,
                "Aprovados",
                String.valueOf(passedTests)
        );

        appendCard(
                html,
                "Reprovados",
                String.valueOf(failedTests)
        );

        appendCard(
                html,
                "Testes críticos",
                String.valueOf(criticalTotal)
        );

        appendCard(
                html,
                "Críticos aprovados",
                String.valueOf(criticalPassed)
        );

        appendCard(
                html,
                "Percentual aprovado",
                String.format(
                        "%.2f%%",
                        percentualAprovacao
                )
        );

        appendCard(
                html,
                "HTTP 5xx inesperados",
                String.valueOf(unexpected5xx)
        );

        html.append("""
                    </div>
                
                    <h2>
                        Avaliação do Quality Gate
                    </h2>
                
                    <table>
                
                        <tr>
                            <th>Critério</th>
                            <th>Resultado</th>
                        </tr>
                """);

        appendCriterionRow(
                html,
                "100% dos testes críticos aprovados",
                criterioCriticos
        );

        appendCriterionRow(
                html,
                "Pelo menos 90% da suíte aprovada",
                criterioPercentual
        );

        appendCriterionRow(
                html,
                "Nenhum HTTP 5xx inesperado",
                criterio5xx
        );

        html.append("</table>");

        html.append(
                "<div class=\"gate-status " +
                        (qualityGateAprovado
                                ? "gate-aprovado"
                                : "gate-reprovado") +
                        "\">"
        );

        html.append("<strong>Quality Gate: ")
                .append(
                        qualityGateAprovado
                                ? "APROVADO"
                                : "REPROVADO"
                )
                .append("</strong>");

        html.append("</div>");

        html.append("""
                    <h2>
                        Resultados dos testes
                    </h2>
                
                    <table>
                
                        <tr>
                            <th>Teste</th>
                            <th>Status</th>
                            <th>Tempo de execução</th>
                        </tr>
                """);

        for (TestResult result : results) {

            html.append("<tr>");

            html.append("<td>")
                    .append(escapeHtml(result.name))
                    .append("</td>");

            html.append("<td class=\"")
                    .append(result.status)
                    .append("\">")
                    .append(result.status)
                    .append("</td>");

            html.append("<td>")
                    .append(result.duration)
                    .append(" ms")
                    .append("</td>");

            html.append("</tr>");
        }

        html.append("""
                    </table>
                
                </body>
                
                </html>
                """);

        Files.writeString(
                Path.of(
                        "target",
                        "test-report.html"
                ),
                html.toString()
        );
    }

    private void appendCriterionRow(
            StringBuilder html,
            String criterio,
            boolean atendido) {

        html.append("<tr>");

        html.append("<td>")
                .append(escapeHtml(criterio))
                .append("</td>");

        html.append("<td class=\"")
                .append(
                        atendido
                                ? "criterio-atendido"
                                : "criterio-nao-atendido"
                )
                .append("\">")
                .append(
                        atendido
                                ? "ATENDIDO"
                                : "NÃO ATENDIDO"
                )
                .append("</td>");

        html.append("</tr>");
    }

    private void appendRow(
            StringBuilder html,
            String label,
            String value) {

        html.append("<tr>");

        html.append("<td>")
                .append(escapeHtml(label))
                .append("</td>");

        html.append("<td>")
                .append(escapeHtml(value))
                .append("</td>");

        html.append("</tr>");
    }

    private void appendCard(
            StringBuilder html,
            String label,
            String value) {

        html.append("<div class=\"card\">");

        html.append(escapeHtml(label));

        html.append("<strong>")
                .append(escapeHtml(value))
                .append("</strong>");

        html.append("</div>");
    }

    private String escapeHtml(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private long countUnexpected5xx() throws IOException {

        Path file = Path.of(
                "target",
                "unexpected-5xx.log"
        );

        if (!Files.exists(file)) {
            return 0;
        }

        try (var lines = Files.lines(file)) {
            return lines.count();
        }
    }
}