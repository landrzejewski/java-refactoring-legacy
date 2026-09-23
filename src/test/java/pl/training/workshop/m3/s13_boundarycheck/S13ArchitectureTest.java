package pl.training.workshop.m3.s13_boundarycheck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Automatyczna ochrona granicy i diagnostyka spójności na plikach źródłowych sceny.
 * Start jest edytowany na żywo, więc dla niego stosujemy "zamrożone naruszenia"
 * (jak FreezingArchRule w ArchUnit): znane naruszenia są tolerowane i można je spłacać,
 * każde NOWE naruszenie zapala czerwone światło. Kroki 1 i 2 sprawdzamy dokładnie.
 * (Ścieżki względem katalogu projektu - tak uruchamia Maven i skrypt warsztatu.)
 */
final class S13ArchitectureTest {
    private static final Path SCENE = Path.of("src/main/java/pl/training/workshop/m3/s13_boundarycheck");
    private static final String PKG = "pl.training.workshop.m3.s13_boundarycheck";
    private static final BoundaryRule DOMAIN_IS_PURE = new BoundaryRule("java.sql.", ".adapter.");
    /** Zamrożone zależności (bez nazwy pliku - przeniesienie kodu nie tworzy "nowego" naruszenia). */
    private static final List<String> FROZEN_START_IMPORTS = List.of(
            "java.sql.Timestamp",
            PKG + ".start.adapter.ScreeningRow");

    @Test
    void startDomainHasNoViolationsBeyondTheFrozenOnes() {
        List<String> violations = DOMAIN_IS_PURE.violations(SCENE.resolve("start/domain"));
        System.out.println("s13 start - naruszenia granicy: " + violations);
        List<String> imports = violations.stream().map(v -> v.substring(v.indexOf(": ") + 2)).toList();
        assertTrue(FROZEN_START_IMPORTS.containsAll(imports), "nowe naruszenie granicy: " + violations);
    }

    @Test
    void step1StillViolatesTheBoundaryButOnlyInTheMapper() {
        assertEquals(List.of(
                        "ScreeningRowMapper.java: java.sql.Timestamp",
                        "ScreeningRowMapper.java: " + PKG + ".step1.adapter.ScreeningRow"),
                DOMAIN_IS_PURE.violations(SCENE.resolve("step1/domain")));
    }

    @Test
    void step2DomainIsFreeOfTechnology() {
        assertEquals(List.of(), DOMAIN_IS_PURE.violations(SCENE.resolve("step2/domain")));
    }

    @Test
    void startServiceCohesionDoesNotGetWorse() {
        var result = new CohesionProbe().analyze(SCENE.resolve("start/domain/ScreeningService.java"));
        System.out.println("s13 start - LCOM4 ScreeningService: " + result);
        assertTrue(result.lcom4() <= 2, "spojnosc sie pogorszyla: " + result);
    }

    @Test
    void step1ClassesAreCohesive() {
        var probe = new CohesionProbe();
        assertEquals(new CohesionProbe.Result(1, List.of("isMorning, price")),
                probe.analyze(SCENE.resolve("step1/domain/ScreeningService.java")));
        assertEquals(new CohesionProbe.Result(1, List.of("fromRow, toRow")),
                probe.analyze(SCENE.resolve("step1/domain/ScreeningRowMapper.java")));
    }
}
