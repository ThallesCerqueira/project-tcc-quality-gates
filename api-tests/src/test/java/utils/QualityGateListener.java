package utils;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class QualityGateListener implements TestExecutionListener {

    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;
    private int abortedTests = 0;

    private int criticalTotal = 0;
    private int criticalPassed = 0;

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {

        if (!testIdentifier.isTest()) {
            return;
        }

        totalTests++;

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

        } catch (IOException e) {
            throw new RuntimeException(
                    "Erro ao gerar dados do Quality Gate",
                    e
            );
        }
    }
}