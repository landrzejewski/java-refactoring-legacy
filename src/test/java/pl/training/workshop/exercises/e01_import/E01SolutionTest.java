package pl.training.workshop.exercises.e01_import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import pl.training.workshop.exercises.e01_import.step2.CsvRepertoireImporter;
import pl.training.workshop.exercises.e01_import.step2.Importers;
import pl.training.workshop.exercises.e01_import.step2.JsonLineParser;
import pl.training.workshop.exercises.e01_import.step2.JsonLinesRepertoireImporter;
import pl.training.workshop.exercises.e01_import.step2.LineParser;
import pl.training.workshop.exercises.e01_import.step2.RawScreening;
import pl.training.workshop.exercises.e01_import.step2.RepertoireImporter;

/**
 * Kontrakt rozwiązania: szkielet w jednym miejscu i chroniony, krok zależny od formatu jako osobny
 * produkt, importer konkretnego formatu tylko tworzy i podpisuje, a rozszerzenia zna jedno miejsce.
 */
final class E01SolutionTest {
    @Test
    void skeletonIsFinalSoNoFormatCanReorderTheSteps() throws NoSuchMethodException {
        Method template = RepertoireImporter.class.getMethod("importLines", List.class);
        assertTrue(Modifier.isFinal(template.getModifiers()));
    }

    @Test
    void newFormatIsOneParserAndOneCreatorAndReusesSortingAndPricing() {
        RepertoireImporter tsv = new RepertoireImporter() {
            @Override
            public String label() {
                return "TSV";
            }

            @Override
            protected LineParser createLineParser() {
                return new LineParser() {
                    @Override
                    public boolean isComment(String line) {
                        return line.startsWith(";");
                    }

                    @Override
                    public RawScreening parse(String line, int number) {
                        String[] fields = line.split("\t");
                        return new RawScreening(fields[0], fields[1], fields[2], fields[3]);
                    }
                };
            }
        };
        List<ScreeningEntry> entries = tsv.importLines(List.of("; komentarz", "Diuna\tIMAX\t1\t20:30", "Amator\t2D\t3\t09:00"));
        assertEquals("[09:00 Amator (2D, sala 3) 20.00, 20:30 Diuna (IMAX, sala 1) 40.00]", entries.toString());
    }

    @Test
    void lineParserIsTestableWithoutAnyImporter() {
        JsonLineParser parser = new JsonLineParser();
        assertEquals(new RawScreening("Diuna", "IMAX", "1", "20:30"),
                parser.parse("{\"title\": \"Diuna\", \"format\": \"IMAX\", \"hall\": \"1\", \"start\": \"20:30\"}", 7));
        var error = assertThrows(IllegalArgumentException.class, () -> parser.parse("{\"title\": \"Diuna\"}", 7));
        assertEquals("line 7: missing field: format", error.getMessage());
    }

    @Test
    void concreteImportersOnlyCreateAndLabel() {
        for (Class<?> creator : List.of(CsvRepertoireImporter.class, JsonLinesRepertoireImporter.class)) {
            Set<String> declared = Arrays.stream(creator.getDeclaredMethods())
                    .filter(method -> !method.isSynthetic())
                    .map(Method::getName)
                    .collect(Collectors.toSet());
            assertEquals(Set.of("label", "createLineParser"), declared, creator.getSimpleName());
        }
    }

    @Test
    void fileExtensionsAreKnownInOnePlace() {
        assertEquals("CSV", Importers.forFile("piatek.csv").label());
        assertEquals("JSONL", Importers.forFile("piatek.jsonl").label());
        var error = assertThrows(IllegalArgumentException.class, () -> Importers.forFile("piatek.xml"));
        assertEquals("unsupported file: piatek.xml", error.getMessage());
    }
}
