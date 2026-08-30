package pl.training.module8.tooling;

import java.util.Objects;
import java.util.Optional;

import javax.tools.Diagnostic;

public record CompilationDiagnostic(
        Diagnostic.Kind kind,
        Optional<String> code,
        long lineNumber,
        long columnNumber) {

    public CompilationDiagnostic {
        Objects.requireNonNull(kind, "kind");
        code = Objects.requireNonNull(code, "code");
        if (code.filter(String::isBlank).isPresent()) {
            throw new IllegalArgumentException("code must not be blank");
        }
        if (lineNumber != Diagnostic.NOPOS && lineNumber < 1) {
            throw new IllegalArgumentException(
                    "lineNumber must be Diagnostic.NOPOS or positive");
        }
        if (columnNumber != Diagnostic.NOPOS && columnNumber < 1) {
            throw new IllegalArgumentException(
                    "columnNumber must be Diagnostic.NOPOS or positive");
        }
    }
}
