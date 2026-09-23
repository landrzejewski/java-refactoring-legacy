package pl.training.workshop.m8.s07_adr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Wykonywalny model decyzji: start narusza ADR-0007, kolejne kroki doprowadzają do zgodności.
 * Ścieżki względem katalogu roboczego = katalog główny repozytorium (mvn test, isolated-test.sh).
 */
final class S07SolutionTest {
    private static final Path SCENE = Path.of("src/main/java/pl/training/workshop/m8/s07_adr");

    @Test
    void startViolatesBothRules() {
        List<String> violations = ArchitectureRules.violations(variant("start"));
        assertTrue(violations.contains("ADR-0007/R1 TicketPricing.java:3"), violations.toString());
        assertEquals(List.of("ADR-0007/R1", "ADR-0007/R2"), ruleIds(violations));
    }

    @Test
    void step1RemovesDependencyOnNotification() {
        assertEquals(List.of("ADR-0007/R2"), ruleIds(ArchitectureRules.violations(variant("step1"))));
    }

    @Test
    void step2CompliesWithTheDecision() {
        assertEquals(List.of(), ArchitectureRules.violations(variant("step2")));
    }

    @Test
    void adrDocumentsEveryExecutableRule() throws IOException {
        String adr = Files.readString(SCENE.resolve("ADR-0007-cennik-jako-czysty-modul.md"));
        assertTrue(adr.contains("**Status:** Zaakceptowana"));
        for (ArchitectureRules.Rule rule : ArchitectureRules.Rule.values()) {
            String shortId = rule.id().substring(rule.id().indexOf('/') + 1);
            assertTrue(adr.contains("**" + shortId + ".**"), "ADR nie opisuje reguły " + rule.id());
        }
    }

    private static Path variant(String name) {
        Path dir = SCENE.resolve(name);
        assertTrue(Files.isDirectory(dir), "uruchom testy z katalogu głównego repozytorium, brak " + dir.toAbsolutePath());
        return dir;
    }

    private static List<String> ruleIds(List<String> violations) {
        return violations.stream().map(v -> v.substring(0, v.indexOf(' '))).distinct().sorted().toList();
    }
}
