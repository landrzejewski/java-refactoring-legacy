package pl.training.module8.tooling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.Set;

import javax.tools.Diagnostic;

import org.junit.jupiter.api.Test;

final class InMemoryJavaCompilerTest {
    private static final String CLEAN_SOURCE = """
            package example;

            import java.util.List;

            public final class TypedNames {
                public int count(List<?> names) {
                    return names.size();
                }
            }
            """;

    private static final String RAW_SOURCE = """
            package example;

            import java.util.List;

            public final class RawNames {
                public int count(List names) {
                    return names.size();
                }
            }
            """;

    private final InMemoryJavaCompiler compiler =
            new InMemoryJavaCompiler();

    @Test
    void compilesTypedSourceAndKeepsGeneratedBytecodeInMemory() {
        CompilationResult result = compiler.compile(
                "example.TypedNames",
                CLEAN_SOURCE,
                WarningPolicy.TREAT_WARNINGS_AS_ERRORS);

        assertTrue(result.successful());
        assertTrue(result.diagnostics().isEmpty());
        assertEquals(Set.of("example.TypedNames"),
                result.generatedClassNames());
    }

    @Test
    void reportsRawTypeByKindAndCompilerCodeWithoutRejectingSource() {
        CompilationResult result = compiler.compile(
                "example.RawNames",
                RAW_SOURCE,
                WarningPolicy.ALLOW_WARNINGS);

        assertTrue(result.successful());
        assertTrue(result.hasDiagnostic(
                Diagnostic.Kind.WARNING,
                "compiler.warn.raw.class.use"));
        assertEquals(Set.of("example.RawNames"),
                result.generatedClassNames());
    }

    @Test
    void rejectsTheSameRawTypeWhenWarningsAreErrors() {
        CompilationResult result = compiler.compile(
                "example.RawNames",
                RAW_SOURCE,
                WarningPolicy.TREAT_WARNINGS_AS_ERRORS);

        assertFalse(result.successful());
        assertTrue(result.hasDiagnostic(
                Diagnostic.Kind.WARNING,
                "compiler.warn.raw.class.use"));
        assertTrue(result.hasDiagnostic(
                Diagnostic.Kind.ERROR,
                "compiler.err.warnings.and.werror"));
    }

    @Test
    void validatesCompilationRequest() {
        assertThrows(NullPointerException.class,
                () -> compiler.compile(
                        null, CLEAN_SOURCE, WarningPolicy.ALLOW_WARNINGS));
        assertThrows(IllegalArgumentException.class,
                () -> compiler.compile(
                        "not a name", CLEAN_SOURCE,
                        WarningPolicy.ALLOW_WARNINGS));
        assertThrows(NullPointerException.class,
                () -> compiler.compile(
                        "example.TypedNames", null,
                        WarningPolicy.ALLOW_WARNINGS));
        assertThrows(IllegalArgumentException.class,
                () -> compiler.compile(
                        "example.TypedNames", "  \n",
                        WarningPolicy.ALLOW_WARNINGS));
        assertThrows(NullPointerException.class,
                () -> compiler.compile(
                        "example.TypedNames", CLEAN_SOURCE, null));
    }

    @Test
    void diagnosticsCanRepresentAnAbsentImplementationSpecificCode() {
        var diagnostic = new CompilationDiagnostic(
                Diagnostic.Kind.NOTE,
                Optional.empty(),
                Diagnostic.NOPOS,
                Diagnostic.NOPOS);

        assertEquals(Optional.empty(), diagnostic.code());
        assertThrows(
                NullPointerException.class,
                () -> new CompilationDiagnostic(
                        Diagnostic.Kind.NOTE,
                        null,
                        Diagnostic.NOPOS,
                        Diagnostic.NOPOS));
        assertThrows(
                IllegalArgumentException.class,
                () -> new CompilationDiagnostic(
                        Diagnostic.Kind.NOTE,
                        Optional.of(" "),
                        Diagnostic.NOPOS,
                        Diagnostic.NOPOS));
        assertThrows(
                IllegalArgumentException.class,
                () -> new CompilationDiagnostic(
                        Diagnostic.Kind.NOTE,
                        Optional.empty(),
                        0,
                        1));
        assertThrows(
                IllegalArgumentException.class,
                () -> new CompilationDiagnostic(
                        Diagnostic.Kind.NOTE,
                        Optional.empty(),
                        Diagnostic.NOPOS - 1,
                        1));
        assertThrows(
                IllegalArgumentException.class,
                () -> new CompilationDiagnostic(
                        Diagnostic.Kind.NOTE,
                        Optional.empty(),
                        1,
                        0));
        assertThrows(
                IllegalArgumentException.class,
                () -> new CompilationDiagnostic(
                        Diagnostic.Kind.NOTE,
                        Optional.empty(),
                        1,
                        Diagnostic.NOPOS - 1));
    }
}
