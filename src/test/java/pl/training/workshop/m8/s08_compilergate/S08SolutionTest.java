package pl.training.workshop.m8.s08_compilergate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Bramka kompilatora na kodzie sceny (-Xlint:all -Werror): każdy krok usuwa jedną klasę ostrzeżeń.
 * Ścieżki względem katalogu roboczego = katalog główny repozytorium.
 */
final class S08SolutionTest {
    private static final Path SCENE = Path.of("src/main/java/pl/training/workshop/m8/s08_compilergate");

    @Test
    void startFailsTheGateWithFourKindsOfWarnings() {
        CompilerGate.Result result = CompilerGate.check(variant("start"));
        assertFalse(result.passed());
        assertEquals(List.of("deprecation", "fallthrough", "rawtypes", "unchecked"), result.categories());
    }

    @Test
    void step1RemovesRawTypesAndUncheckedOperations() {
        CompilerGate.Result result = CompilerGate.check(variant("step1"));
        assertFalse(result.passed());
        assertEquals(List.of("deprecation", "fallthrough"), result.categories());
    }

    @Test
    void step2StopsUsingDeprecatedApi() {
        CompilerGate.Result result = CompilerGate.check(variant("step2"));
        assertFalse(result.passed());
        assertEquals(List.of("fallthrough"), result.categories());
    }

    @Test
    void step3PassesTheGate() {
        CompilerGate.Result result = CompilerGate.check(variant("step3"));
        assertEquals(List.of(), result.warnings());
        assertTrue(result.passed());
    }

    @Test
    void warningsPointToFileAndLine() {
        List<String> warnings = CompilerGate.check(variant("step2")).warnings().stream()
                .map(CompilerGate.Warning::toString).toList();
        assertEquals(List.of("[fallthrough] OccupancyReport.java:13"), warnings);
    }

    private static Path variant(String name) {
        Path dir = SCENE.resolve(name);
        assertTrue(Files.isDirectory(dir), "uruchom testy z katalogu głównego repozytorium, brak " + dir.toAbsolutePath());
        return dir;
    }
}
