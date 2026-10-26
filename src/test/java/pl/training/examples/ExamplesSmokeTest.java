package pl.training.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Uruchamia każdy przykład zarejestrowany w {@link Module4Examples} z przechwyconym
 * wyjściem (System.out, System.err i java.util.logging) oraz pustym wejściem.
 */
@ResourceLock(Resources.SYSTEM_OUT)
@ResourceLock(Resources.SYSTEM_ERR)
@ResourceLock("java.lang.System.in")
final class ExamplesSmokeTest {

    static Stream<String> exampleKeys() {
        return Module4Examples.examples().stream().map(Module4Examples.Example::key);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("exampleKeys")
    void exampleRunsWithoutException(String key) {
        Captured<Boolean> captured = capture(() -> Module4Examples.tryRun(key), "");

        assertTrue(captured.result());
        assertFalse(captured.output().isBlank());
    }

    @Test
    void unknownKeyIsNotFound() {
        assertFalse(Module4Examples.tryRun("no-such-example"));
    }

    @Test
    void unknownKeyReturnsExitCodeOneAndPrintsUsage() {
        Captured<Integer> captured = capture(
                () -> Module4Examples.execute(new String[] {"no-such-example"}), "");

        assertEquals(1, captured.result());
        assertTrue(captured.output().contains("Unknown example: no-such-example"));
        assertTrue(captured.output().contains("Usage:"));
    }

    @Test
    void decoratorUsesDefaultTextWhenStdinIsEmpty() {
        Captured<Boolean> captured = capture(() -> Module4Examples.tryRun("decorator"), "");

        assertTrue(captured.output().contains("INFO: hello_decorator_pattern"));
    }

    @Test
    void decoratorTransformsTheLineReadFromStdin() {
        Captured<Boolean> captured = capture(
                () -> Module4Examples.tryRun("decorator"), "Hello World Foo\n");

        assertTrue(captured.output().contains("INFO: hello_world_foo"));
    }

    @Test
    void mainWithoutArgumentsListsEveryExampleKey() {
        Captured<Integer> captured = capture(() -> Module4Examples.execute(new String[0]), "");

        assertEquals(0, captured.result());
        Module4Examples.examples().forEach(example ->
                assertTrue(captured.output().contains(example.key()), example.key()));
    }

    @Test
    void mainRunsAllExamplesInOrder() {
        Captured<Integer> captured = capture(() -> Module4Examples.execute(new String[] {"all"}), "");

        assertEquals(0, captured.result());
        List<String> headers = captured.output().lines()
                .filter(line -> line.startsWith("=== "))
                .toList();
        assertEquals(
                Module4Examples.examples().stream()
                        .map(example -> "=== " + example.key() + " ===")
                        .toList(),
                headers);
    }

    private record Captured<T>(T result, String output) {
    }

    private static <T> Captured<T> capture(Supplier<T> action, String input) {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        InputStream originalIn = System.in;
        Logger root = Logger.getLogger("");
        Handler[] originalHandlers = root.getHandlers();
        var output = new ByteArrayOutputStream();
        var stream = new PrintStream(output, true, StandardCharsets.UTF_8);
        Handler logCapture = new Handler() {
            @Override
            public void publish(LogRecord record) {
                stream.println(record.getLevel() + ": " + record.getMessage());
            }

            @Override
            public void flush() {
                stream.flush();
            }

            @Override
            public void close() {
            }
        };
        try {
            for (Handler handler : originalHandlers) {
                root.removeHandler(handler);
            }
            root.addHandler(logCapture);
            System.setOut(stream);
            System.setErr(stream);
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            T result = action.get();
            return new Captured<>(result, output.toString(StandardCharsets.UTF_8));
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
            System.setIn(originalIn);
            root.removeHandler(logCapture);
            for (Handler handler : originalHandlers) {
                root.addHandler(handler);
            }
        }
    }
}
