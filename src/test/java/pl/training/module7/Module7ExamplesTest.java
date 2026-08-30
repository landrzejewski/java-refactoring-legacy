package pl.training.module7;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

final class Module7ExamplesTest {
    @Test
    void runsOneDeterministicExampleForEveryTopic() {
        assertEquals(List.of(
                "Break Dependencies: ALLOWED",
                "Extract Method Object: 45/MEDIUM",
                "Break Responsibilities: deployments=3;failures=1;"
                        + "avgLeadTimeMinutes=18",
                "Remove Duplication: image:42",
                "Break Method: 10|api|sha-api,20|worker|sha-worker",
                "Introduce Parameter Object: 165s",
                "Remove Arrowhead: ELIGIBLE",
                "Design by Contract: remaining=7",
                "Remove Double Negative: true",
                "Remove God Class: rel-42/1/1/1",
                "Remove Boolean Parameter: deployed:rel-42",
                "Remove Middle Man: dep-42 -> RUNNING",
                "Return ASAP: api.jar"),
                Module7Examples.runExamples());
    }

    @Test
    void returnedResultsCannotBeModified() {
        List<String> results = Module7Examples.runExamples();

        assertThrows(
                UnsupportedOperationException.class,
                () -> results.add("unexpected"));
    }

    @Test
    @ResourceLock(Resources.SYSTEM_OUT)
    void mainPrintsEveryResultOnItsOwnLine() {
        PrintStream originalOutput = System.out;
        var bytes = new ByteArrayOutputStream();

        try (var capturedOutput = new PrintStream(
                bytes, true, StandardCharsets.UTF_8)) {
            System.setOut(capturedOutput);
            assertDoesNotThrow(() -> Module7Examples.main(new String[0]));
        } finally {
            System.setOut(originalOutput);
        }

        String expected = String.join(
                System.lineSeparator(), Module7Examples.runExamples())
                + System.lineSeparator();
        assertEquals(expected, bytes.toString(StandardCharsets.UTF_8));
    }
}
