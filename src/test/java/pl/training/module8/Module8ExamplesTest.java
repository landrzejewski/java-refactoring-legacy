package pl.training.module8;

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

final class Module8ExamplesTest {
    @Test
    void runsOneDeterministicExampleForEveryTopic() {
        assertEquals(List.of(
                "Stopniowa migracja: 53.97/Agreement",
                "Boy Scout: Successful: 1/2",
                "Code review: ready=true",
                "Dokumentowanie: # ADR-0042: Use Branch by Abstraction",
                "Narzędzia: compiled=true",
                "Zarządzanie ryzykiem: ADVANCE"),
                Module8Examples.runExamples());
    }

    @Test
    void returnedResultsCannotBeModified() {
        List<String> results = Module8Examples.runExamples();

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
            assertDoesNotThrow(() -> Module8Examples.main(new String[0]));
        } finally {
            System.setOut(originalOutput);
        }

        String expected = String.join(
                System.lineSeparator(), Module8Examples.runExamples())
                + System.lineSeparator();
        assertEquals(expected, bytes.toString(StandardCharsets.UTF_8));
    }
}
