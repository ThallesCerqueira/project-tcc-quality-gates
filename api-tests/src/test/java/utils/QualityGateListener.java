package utils;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {

        if (!testIdentifier.isTest()) {
            return;
        }

        totalTests++;

        long start = startTimes.getOrDefault(
                testIdentifier.getUniqueId(),
                System.currentTimeMillis()
        );

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

        boolean critical = testIdentifier.getTags().stream().anyMatch(tag -> tag.getName().equals("critical"));

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

            String result =
                    "total_tests=" + totalTests + "\n" +
                            "passed_tests=" + passedTests + "\n" +
                            "failed_tests=" + failedTests + "\n" +
                            "aborted_tests=" + abortedTests + "\n" +
                            "critical_total=" + criticalTotal + "\n" +
                            "critical_passed=" + criticalPassed + "\n";

            Files.writeString(
                    Path.of("target", "quality-gate.properties"),
                    result
            );

            generateHtmlReport();

        } catch (IOException e) {
            throw new RuntimeException(
                    "Erro ao gerar dados do Quality Gate",
                    e
            );
        }
    }

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {

        if (testIdentifier.isTest()) {
            startTimes.put(
                    testIdentifier.getUniqueId(),
                    System.currentTimeMillis()
            );
        }
    }

    private void generateHtmlReport() throws IOException {

        StringBuilder html = new StringBuilder();

        String dataExecucao = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        String versao = System.getenv().getOrDefault(
                "APP_VERSION",
                "Execução local"
        );

        String branch = System.getenv().getOrDefault(
                "GITHUB_REF_NAME",
                "local"
        );

        String commit = System.getenv().getOrDefault(
                "GITHUB_SHA",
                "local"
        );

        if (!commit.equals("local") && commit.length() > 7) {
            commit = commit.substring(0, 7);
        }

        String pipeline = System.getenv().getOrDefault(
                "GITHUB_RUN_NUMBER",
                "local"
        );

        String ambiente = System.getenv().getOrDefault(
                "BASE_URL",
                "http://localhost:3002"
        );

        html.append("""
        <!DOCTYPE html>
        <html lang="pt-BR">
        <head>
            <meta charset="UTF-8">
            <title>Relatório de Testes</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    margin: 40px;
                }

                table {
                    border-collapse: collapse;
                    width: 100%;
                }

                th, td {
                    border: 1px solid #ccc;
                    padding: 10px;
                    text-align: left;
                }

                th {
                    background: #f2f2f2;
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
            </style>
        </head>

        <body>

        <h1>Relatório de Execução dos Testes</h1>
        
                html.append(""\"
                    <h2>Identificação da execução</h2>
                
                    <table>
                        <tr>
                            <th>Informação</th>
                            <th>Valor</th>
                        </tr>
                    ""\");
                
                html.append("<tr><td>Data/Hora</td><td>")
                        .append(dataExecucao)
                        .append("</td></tr>");
                
                html.append("<tr><td>Versão</td><td>")
                        .append(versao)
                        .append("</td></tr>");
                
                html.append("<tr><td>Branch</td><td>")
                        .append(branch)
                        .append("</td></tr>");
                
                html.append("<tr><td>Commit</td><td>")
                        .append(commit)
                        .append("</td></tr>");
                
                html.append("<tr><td>Execução do pipeline</td><td>#")
                        .append(pipeline)
                        .append("</td></tr>");
                
                html.append("<tr><td>Ambiente</td><td>")
                        .append(ambiente)
                        .append("</td></tr>");
                
                html.append(""\"
                    </table>
                
                    <br>
                
                    <h2>Resultados dos testes</h2>
                    ""\");

        <table>

        <tr>
            <th>Teste</th>
            <th>Status</th>
            <th>Tempo</th>
        </tr>
        """);

        for (TestResult result : results) {

            html.append("<tr>");

            html.append("<td>")
                    .append(result.name)
                    .append("</td>");

            html.append("<td class='")
                    .append(result.status)
                    .append("'>")
                    .append(result.status)
                    .append("</td>");

            html.append("<td>")
                    .append(result.duration)
                    .append(" ms</td>");

            html.append("</tr>");
        }

        html.append("""
        </table>
        </body>
        </html>
        """);

        Files.writeString(
                Path.of("target", "test-report.html"),
                html.toString()
        );
    }
}