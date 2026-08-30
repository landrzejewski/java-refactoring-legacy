package pl.training.module8.tooling;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.lang.model.SourceVersion;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public final class InMemoryJavaCompiler {
    private static final List<String> BASE_OPTIONS = List.of(
            "--release", "25",
            "-proc:none",
            "-Xlint:rawtypes");

    public CompilationResult compile(
            String binaryName,
            String source,
            WarningPolicy warningPolicy) {

        validateBinaryName(binaryName);
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(warningPolicy, "warningPolicy");
        if (source.isBlank()) {
            throw new IllegalArgumentException("source must not be blank");
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException(
                    "System Java compiler is unavailable; run on a JDK");
        }

        var diagnostics = new DiagnosticCollector<JavaFileObject>();
        var sourceFile = new SourceFile(binaryName, source);
        List<String> options = compilerOptions(warningPolicy);

        StandardJavaFileManager standardFileManager =
                compiler.getStandardFileManager(
                        diagnostics, Locale.ROOT, UTF_8);
        try (var memoryFileManager =
                new MemoryFileManager(standardFileManager)) {

            boolean successful = Boolean.TRUE.equals(compiler.getTask(
                    null,
                    memoryFileManager,
                    diagnostics,
                    options,
                    null,
                    List.of(sourceFile)).call());

            List<CompilationDiagnostic> stableDiagnostics = diagnostics
                    .getDiagnostics()
                    .stream()
                    .map(diagnostic -> new CompilationDiagnostic(
                            diagnostic.getKind(),
                            Optional.ofNullable(diagnostic.getCode()),
                            diagnostic.getLineNumber(),
                            diagnostic.getColumnNumber()))
                    .toList();

            return new CompilationResult(
                    successful,
                    stableDiagnostics,
                    memoryFileManager.generatedClassNames());
        } catch (IOException failure) {
            throw new IllegalStateException(
                    "Could not close the in-memory compiler", failure);
        }
    }

    private static List<String> compilerOptions(WarningPolicy warningPolicy) {
        var options = new ArrayList<>(BASE_OPTIONS);
        if (warningPolicy == WarningPolicy.TREAT_WARNINGS_AS_ERRORS) {
            options.add("-Werror");
        }
        return List.copyOf(options);
    }

    private static void validateBinaryName(String binaryName) {
        Objects.requireNonNull(binaryName, "binaryName");
        if (!SourceVersion.isName(binaryName)) {
            throw new IllegalArgumentException(
                    "binaryName must be a valid Java binary name");
        }
    }

    private static final class SourceFile extends SimpleJavaFileObject {
        private final String source;

        private SourceFile(String binaryName, String source) {
            super(uriFor(binaryName, Kind.SOURCE), Kind.SOURCE);
            this.source = source;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return source;
        }
    }

    private static final class ClassFile extends SimpleJavaFileObject {
        private final ByteArrayOutputStream bytecode =
                new ByteArrayOutputStream();

        private ClassFile(String binaryName) {
            super(uriFor(binaryName, Kind.CLASS), Kind.CLASS);
        }

        @Override
        public OutputStream openOutputStream() {
            bytecode.reset();
            return bytecode;
        }
    }

    private static final class MemoryFileManager
            extends ForwardingJavaFileManager<StandardJavaFileManager> {
        private final Map<String, ClassFile> generatedClasses =
                new LinkedHashMap<>();

        private MemoryFileManager(StandardJavaFileManager fileManager) {
            super(fileManager);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(
                JavaFileManager.Location location,
                String className,
                JavaFileObject.Kind kind,
                FileObject sibling) {

            if (kind != JavaFileObject.Kind.CLASS) {
                throw new IllegalArgumentException(
                        "Only in-memory class output is supported");
            }

            var classFile = new ClassFile(className);
            generatedClasses.put(className, classFile);
            return classFile;
        }

        private Set<String> generatedClassNames() {
            return Set.copyOf(generatedClasses.keySet());
        }
    }

    private static URI uriFor(
            String binaryName,
            JavaFileObject.Kind kind) {
        return URI.create("memory:///"
                + binaryName.replace('.', '/')
                + kind.extension);
    }
}
