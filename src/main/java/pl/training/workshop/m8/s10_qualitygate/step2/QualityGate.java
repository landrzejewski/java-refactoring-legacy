package pl.training.workshop.m8.s10_qualitygate.step2;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import pl.training.workshop.m8.s10_qualitygate.GateInput;

/**
 * Krok 2: + ostrzeżenia kompilatora (javax.tools, -Xlint:all). Każde ostrzeżenie to wynik
 * z kategorią i pozycją; bramka nie przepuszcza nowych ostrzeżeń w domenie.
 */
public final class QualityGate {
    /** Lista wyników bramki; pusta lista = bramka przepuszcza zmianę. */
    public List<String> evaluate(GateInput input) {
        List<String> findings = new ArrayList<>();
        compileSources(input, findings);
        scanSources(input, findings);
        return findings;
    }

    public boolean passes(GateInput input) {
        return evaluate(input).isEmpty();
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
