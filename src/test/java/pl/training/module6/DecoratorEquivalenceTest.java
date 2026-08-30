package pl.training.module6;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.module6.decorator.after.AuditedDeploymentRunner;
import pl.training.module6.decorator.after.BasicDeploymentRunner;
import pl.training.module6.decorator.before.LegacyDeploymentRunner;

final class DecoratorEquivalenceTest {
    @Test
    void preservesResultAndAuditOrderOnSuccess() {
        List<String> beforeAudit = new ArrayList<>();
        List<String> afterAudit = new ArrayList<>();

        String before = new LegacyDeploymentRunner(beforeAudit::add).run("rel-42");
        String after = new AuditedDeploymentRunner(
                new BasicDeploymentRunner(), afterAudit::add).run("rel-42");

        assertEquals(before, after);
        assertEquals(beforeAudit, afterAudit);
    }

    @Test
    void preservesAuditOrderAndPropagatesTheSameFailure() {
        List<String> audit = new ArrayList<>();
        RuntimeException failure = new IllegalStateException("gateway unavailable");
        var runner = new AuditedDeploymentRunner(
                ignored -> {
                    throw failure;
                },
                audit::add);

        RuntimeException propagated = assertThrows(
                RuntimeException.class,
                () -> runner.run("rel-42"));

        assertSame(failure, propagated);
        assertEquals(
                List.of("start:rel-42", "failure:rel-42:IllegalStateException"),
                audit);
    }

    @Test
    void invalidInputPreservesFailureAndAuditTrace() {
        List<String> beforeAudit = new ArrayList<>();
        List<String> afterAudit = new ArrayList<>();

        RuntimeException beforeFailure = assertThrows(
                RuntimeException.class,
                () -> new LegacyDeploymentRunner(beforeAudit::add).run(" "));
        RuntimeException afterFailure = assertThrows(
                RuntimeException.class,
                () -> new AuditedDeploymentRunner(
                        new BasicDeploymentRunner(), afterAudit::add).run(" "));

        assertEquals(beforeFailure.getClass(), afterFailure.getClass());
        assertEquals(beforeFailure.getMessage(), afterFailure.getMessage());
        assertEquals(beforeAudit, afterAudit);
    }
}
