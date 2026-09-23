package pl.training.workshop.m8.s13_livingdocs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Dokumentacja żywa: ROUTING.md musi zgadzać się z kodem. Ścieżki względem katalogu roboczego =
 * katalog główny repozytorium (mvn test, isolated-test.sh).
 */
final class S13SolutionTest {
    private static final Path SCENE = Path.of("src/main/java/pl/training/workshop/m8/s13_livingdocs");

    @Test
    void startDocumentationHasDriftedFromCode() throws IOException {
        Map<String, String> code = new LinkedHashMap<>();
        pl.training.workshop.m8.s13_livingdocs.start.Routing.routes()
                .forEach(route -> code.put(route.operation(), route.target()));
        assertEquals(List.of("report: dokument mowi legacy, kod mowi new", "cancel: brak w dokumencie"),
                drift(read("start"), code));
    }

    @Test
    void step1DocumentIsGeneratedFromCode() throws IOException {
        assertEquals(pl.training.workshop.m8.s13_livingdocs.step1.RoutingDoc.render(
                pl.training.workshop.m8.s13_livingdocs.step1.Routing.routes()), read("step1"),
                "ROUTING.md nieaktualny - uruchom RoutingDoc.main z katalogu repozytorium");
    }

    @Test
    void step2DocumentIsGeneratedFromCode() throws IOException {
        assertEquals(pl.training.workshop.m8.s13_livingdocs.step2.RoutingDoc.render(
                pl.training.workshop.m8.s13_livingdocs.step2.Routing.routes()), read("step2"),
                "ROUTING.md nieaktualny - uruchom RoutingDoc.main z katalogu repozytorium");
    }

    @Test
    void step2EveryTransitionalRouteHasOwnerAndRemovalCriterion() {
        for (var route : pl.training.workshop.m8.s13_livingdocs.step2.Routing.routes()) {
            assertFalse(route.owner().isBlank(), route.operation());
            if (route.target().equals("legacy")) {
                assertFalse(route.removeWhen().equals("-"), "trasa do legacy bez kryterium usunięcia: " + route);
            }
        }
    }

    @Test
    void documentLocationFollowsThePackage() {
        assertEquals(SCENE.resolve("step2/ROUTING.md"),
                pl.training.workshop.m8.s13_livingdocs.step2.RoutingDoc.location());
    }

    private static String read(String variant) throws IOException {
        Path doc = SCENE.resolve(variant).resolve("ROUTING.md");
        assertTrue(Files.isRegularFile(doc), "uruchom testy z katalogu głównego repozytorium, brak " + doc.toAbsolutePath());
        return Files.readString(doc);
    }

    /** Porównuje dwie pierwsze kolumny tabeli z dokumentu z routingiem z kodu. */
    private static List<String> drift(String markdown, Map<String, String> code) {
        Map<String, String> documented = new LinkedHashMap<>();
        for (String line : markdown.lines().toList()) {
            String[] cells = line.split("\\|");
            if (line.startsWith("|") && cells.length > 2 && !cells[1].isBlank()
                    && !cells[1].strip().equals("Operacja") && !cells[1].strip().startsWith("---")) {
                documented.put(cells[1].strip(), cells[2].strip());
            }
        }
        List<String> problems = new ArrayList<>();
        code.forEach((operation, target) -> {
            String inDoc = documented.get(operation);
            if (inDoc == null) {
                problems.add(operation + ": brak w dokumencie");
            } else if (!inDoc.equals(target)) {
                problems.add(operation + ": dokument mowi " + inDoc + ", kod mowi " + target);
            }
        });
        return problems;
    }
}
