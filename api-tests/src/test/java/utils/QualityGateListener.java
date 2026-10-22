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