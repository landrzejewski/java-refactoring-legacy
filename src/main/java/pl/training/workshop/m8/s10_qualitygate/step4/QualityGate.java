package pl.training.workshop.m8.s10_qualitygate.step4;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import pl.training.workshop.m8.s10_qualitygate.GateInput;

/**
 * Krok 4: + testy zielone - bramka uruchamia metody @Test klasy testowej (refleksja, bez
 * zależności od JUnit) i zgłasza porażki. Lista kontrolna jest teraz w całości wykonywalna.
 */
public final class QualityGate {
    private static final Pattern PUBLIC_METHOD =
            Pattern.compile("public\\s+(?:static\\s+)?[\\w<>\\[\\],]+\\s+(\\w+)\\s*\\(");

    /** Lista wyników bramki; pusta lista = bramka przepuszcza zmianę. */
    public List<String> evaluate(GateInput input) {
        List<String> findings = new ArrayList<>();
        runTests(input, findings);
        compileSources(input, findings);
        scanSources(input, findings);
        checkCoverage(input, findings);
        return findings;
    }

    public boolean passes(GateInput input) {
        return evaluate(input).isEmpty();
    }

    /** Uruchamia metody oznaczone adnotacją o nazwie Test (JUnit albo dowolna inna). */
    private static void runTests(GateInput input, List<String> findings) {
        try {
            Class<?> type = Class.forName(input.testClass());
            List<Method> tests = Arrays.stream(type.getDeclaredMethods())
                    .filter(m -> Arrays.stream(m.getAnnotations())
                            .anyMatch(a -> a.annotationType().getSimpleName().equals("Test")))
                    .sorted((a, b) -> a.getName().compareTo(b.getName()))
                    .toList();
            for (Method test : tests) {
                var constructor = type.getDeclaredConstructor();
                constructor.setAccessible(true);
                test.setAccessible(true);
                try {
                    test.invoke(constructor.newInstance());
                } catch (InvocationTargetException failure) {
                    findings.add("test " + type.getSimpleName() + "." + test.getName() + " nie przechodzi: "
                            + failure.getCause().getClass().getSimpleName());
                }
            }
        } catch (ReflectiveOperationException e) {
            findings.add("test " + input.testClass() + " nie da się uruchomić: " + e);
        }
    }

    /** Kompilacja źródeł domeny z -Xlint:all; każde ostrzeżenie to wynik bramki. */
    private static void compileSources(GateInput input, List<String> findings) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        Path output = tempDir();
        try (StandardJavaFileManager files =
                compiler.getStandardFileManager(diagnostics, Locale.ROOT, StandardCharsets.UTF_8)) {
            compiler.getTask(null, files, diagnostics,
                    List.of("--release", "25", "-proc:none", "-Xlint:all", "-d", output.toString()), null,
                    files.getJavaFileObjectsFromPaths(javaFiles(input.sources()))).call();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            delete(output);
        }
        for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
            if (d.getKind() != Diagnostic.Kind.NOTE && d.getSource() != null) {
                findings.add("kompilator " + Path.of(d.getSource().toUri()).getFileName() + ":"
                        + d.getLineNumber() + " " + d.getCode());
            }
        }
    }

    private static Path tempDir() {
        try {
            return Files.createTempDirectory("quality-gate");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void delete(Path dir) {
        try (Stream<Path> paths = Files.walk(dir)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** TODO/FIXME i wydruki na konsolę w źródłach domeny. */
    private static void scanSources(GateInput input, List<String> findings) {
        for (Path file : javaFiles(input.sources())) {
            List<String> lines = read(file);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                String where = file.getFileName() + ":" + (i + 1);
                if (line.contains("TODO") || line.contains("FIXME")) {
                    findings.add("TODO " + where);
                }
                if (line.contains("System.out") || line.contains("System.err")) {
                    findings.add("System.out " + where);
                }
            }
        }
    }

    /** Przybliżenie pokrycia: publiczna metoda kluczowej klasy musi być wywołana w jej teście. */
    private static void checkCoverage(GateInput input, List<String> findings) {
        String source = String.join("\n", read(input.sources().resolve(input.keyClass() + ".java")));
        String test = String.join("\n", read(input.testSource()));
        Matcher method = PUBLIC_METHOD.matcher(source);
        while (method.find()) {
            if (!test.contains("." + method.group(1) + "(")) {
                findings.add("pokrycie " + input.keyClass() + "." + method.group(1) + " bez testu");
            }
        }
    }

    private static List<Path> javaFiles(Path dir) {
        try (Stream<Path> files = Files.list(dir)) {
            return files.filter(f -> f.toString().endsWith(".java")).sorted().toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static List<String> read(Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
