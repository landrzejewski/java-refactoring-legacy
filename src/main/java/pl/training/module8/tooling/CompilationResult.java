package pl.training.module8.tooling;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import javax.tools.Diagnostic;

public record CompilationResult(
        boolean successful,
        List<CompilationDiagnostic> diagnostics,
        Set<String> generatedClassNames) {

    public CompilationResult {
        diagnostics = List.copyOf(Objects.requireNonNull(
                diagnostics, "diagnostics"));
        generatedClassNames = Collections.unmodifiableSet(new TreeSet<>(
                Objects.requireNonNull(
                        generatedClassNames, "generatedClassNames")));
    }

    public boolean hasDiagnostic(Diagnostic.Kind kind, String code) {
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(code, "code");

        return diagnostics.stream().anyMatch(diagnostic ->
                diagnostic.kind() == kind
                        && diagnostic.code().filter(code::equals).isPresent());
    }
}
