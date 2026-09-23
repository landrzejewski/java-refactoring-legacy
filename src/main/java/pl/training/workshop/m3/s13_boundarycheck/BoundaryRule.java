package pl.training.workshop.m3.s13_boundarycheck;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Narzędzie sceny: najprostszy test architektury bez bibliotek. Skanuje pliki .java
 * w katalogu (np. pakiet domain) i zgłasza importy zawierające zakazany fragment.
 * <p>Ograniczenia (świadome): widzi tylko importy - pełna nazwa klasy w kodzie przejdzie.
 * Silniejsze bramki: ArchUnit, osobne moduły Maven, JPMS (module-info), reguły w CI.
 */
public final class BoundaryRule {
    private final List<String> forbiddenFragments;

    public BoundaryRule(String... forbiddenFragments) {
        this.forbiddenFragments = List.of(forbiddenFragments);
    }

    /** Naruszenia w formacie "Plik.java: zaimportowany.Typ", posortowane po pliku. */
    public List<String> violations(Path sourceDir) {
        try (Stream<Path> files = Files.walk(sourceDir)) {
            return files.filter(path -> path.toString().endsWith(".java"))
                    .sorted()
                    .flatMap(path -> importsOf(path).stream()
                            .filter(this::isForbidden)
                            .map(imported -> path.getFileName() + ": " + imported))
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private boolean isForbidden(String imported) {
        return forbiddenFragments.stream().anyMatch(imported::contains);
    }

    private static List<String> importsOf(Path file) {
        try {
            return Files.readAllLines(file).stream()
                    .map(String::trim)
                    .filter(line -> line.startsWith("import "))
                    .map(line -> line.replaceFirst("^import\\s+(static\\s+)?", "").replace(";", "").trim())
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
