package pl.training.workshop.m8.s08_compilergate;

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

/**
 * Bramka kompilatora: kompiluje wszystkie pliki katalogu przez javax.tools z opcjami
 * {@code --release 25 -Xlint:all -Werror} i zwraca werdykt oraz strukturalną diagnostykę
 * (kategoria lint, plik, linia) zamiast tekstu z konsoli.
 * <p>
 * Uwaga: z -Werror javac przerywa po fazie, w której padło pierwsze ostrzeżenie (np. fallthrough
 * z analizy przepływu już się nie pojawi). Dlatego pełną listę ostrzeżeń zbieramy osobnym
 * przebiegiem bez -Werror, a werdykt bierzemy z przebiegu z -Werror.
 */
public final class CompilerGate {
    public static final List<String> OPTIONS = List.of("--release", "25", "-proc:none", "-Xlint:all");

    public record Warning(String category, String file, long line) {
        @Override
        public String toString() {
            return "[" + category + "] " + file + ":" + line;
        }
    }

    public record Result(boolean passed, List<Warning> warnings) {
        public List<String> categories() {
            return warnings.stream().map(Warning::category).distinct().sorted().toList();
        }
    }

    private CompilerGate() {
    }

    public static Result check(Path sourceDir) {
        List<Path> sources = javaFiles(sourceDir);
        DiagnosticCollector<JavaFileObject> all = new DiagnosticCollector<>();
        compile(sources, List.of(), all);
        boolean passed = compile(sources, List.of("-Werror"), new DiagnosticCollector<>());
        return new Result(passed, warnings(all));
    }

    private static boolean compile(List<Path> sources, List<String> extraOptions,
            DiagnosticCollector<JavaFileObject> diagnostics) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        Path output = createTempDir();
        try (StandardJavaFileManager files =
                compiler.getStandardFileManager(diagnostics, Locale.ROOT, StandardCharsets.UTF_8)) {
            List<String> options = new ArrayList<>(OPTIONS);
            options.addAll(extraOptions);
            options.addAll(List.of("-d", output.toString()));
            return compiler.getTask(null, files, diagnostics, options, null,
                    files.getJavaFileObjectsFromPaths(sources)).call();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            delete(output);
        }
    }

    private static List<Warning> warnings(DiagnosticCollector<JavaFileObject> diagnostics) {
        List<Warning> warnings = new ArrayList<>();
        for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
            boolean warning = d.getKind() == Diagnostic.Kind.WARNING
                    || d.getKind() == Diagnostic.Kind.MANDATORY_WARNING;
            if (warning && d.getSource() != null) {
                String category = lintCategory(d.getCode());
                String file = Path.of(d.getSource().toUri()).getFileName().toString();
                warnings.add(new Warning(category, file, d.getLineNumber()));
            }
        }
        return warnings;
    }

    /** Klucz komunikatu javac (np. compiler.warn.raw.class.use) na nazwę kategorii -Xlint. */
    private static String lintCategory(String code) {
        if (code.contains("deprecated.for.removal")) {
            return "removal";
        } else if (code.contains("deprecated")) {
            return "deprecation";
        } else if (code.contains("raw.class")) {
            return "rawtypes";
        } else if (code.contains("unchecked") || code.equals("compiler.warn.prob.found.req")) {
            return "unchecked";
        } else if (code.contains("fall-through")) {
            return "fallthrough";
        }
        return code;
    }

    private static List<Path> javaFiles(Path dir) {
        try (Stream<Path> files = Files.list(dir)) {
            return files.filter(f -> f.toString().endsWith(".java")).sorted().toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Path createTempDir() {
        try {
            return Files.createTempDirectory("compiler-gate");
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
}
