package pl.training.workshop.exercises.e01_import;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * Import repertuaru: oba formaty, komentarze właściwe dla formatu, sortowanie, numeracja wierszy
 * w komunikatach i każdy błąd walidacji - te same przypadki na start i każdym kroku.
 */
final class E01EquivalenceTest {
    private static final String THREE_SCREENINGS = "pozycji: 3\n"
            + "10:00 Kraina Lodu (3D, sala 2) 27.00\n"
            + "16:15 Amator (2D, sala 3) 25.00\n"
            + "20:30 Diuna (IMAX, sala 1) 40.00";

    @TestFactory
    Stream<DynamicTest> everyStepImportsTheSame() {
        return Scene.<ImportRequest, String>variants()
                .variant("start", r -> run(() -> new pl.training.workshop.exercises.e01_import.start.RepertoireImport().load(r)))
                .variant("step1", r -> run(() -> new pl.training.workshop.exercises.e01_import.step1.RepertoireImport().load(r)))
                .variant("step2", r -> run(() -> new pl.training.workshop.exercises.e01_import.step2.RepertoireImport().load(r)))
                .expect("CSV: komentarz, pusta linia, sortowanie po godzinie, zniżka poranna",
                        csv("# repertuar na piatek", "Diuna;IMAX;1;20:30", "", "Kraina Lodu;3D;2;10:00", "Amator;2D;3;16:15"),
                        "[CSV] " + THREE_SCREENINGS)
                .expect("JSONL: te same seanse, komentarz od //",
                        jsonl("// repertuar na piatek",
                                "{\"title\": \"Diuna\", \"format\": \"IMAX\", \"hall\": \"1\", \"start\": \"20:30\"}",
                                "",
                                "{\"title\":\"Kraina Lodu\",\"format\":\"3D\",\"hall\":\"2\",\"start\":\"10:00\"}",
                                "{\"title\": \"Amator\", \"format\": \"2D\", \"hall\": \"3\", \"start\": \"16:15\"}"),
                        "[JSONL] " + THREE_SCREENINGS)
                .expect("CSV: ta sama godzina sortuje po tytule",
                        csv("Diuna;IMAX;1;18:00", "Amator;2D;3;18:00"),
                        "[CSV] pozycji: 2\n18:00 Amator (2D, sala 3) 25.00\n18:00 Diuna (IMAX, sala 1) 40.00")
                .expect("CSV: spacje wokół pól są przycinane",
                        csv("  Diuna ; IMAX ; 1 ; 20:30  "),
                        "[CSV] pozycji: 1\n20:30 Diuna (IMAX, sala 1) 40.00")
                .expect("CSV: pusty plik", csv(), "[CSV] pozycji: 0")
                .expect("JSONL: kolejność pól w obiekcie nie ma znaczenia",
                        jsonl("{\"start\": \"11:59\", \"hall\": \"4\", \"format\": \"2D\", \"title\": \"Amator\"}"),
                        "[JSONL] pozycji: 1\n11:59 Amator (2D, sala 4) 20.00")
                .expect("CSV: za mało pól", csv("# naglowek", "Diuna;IMAX;20:30"),
                        "ERROR: line 2: expected 4 fields, got 3")
                .expect("CSV: komentarz w stylu JSONL to błąd", csv("// to nie jest komentarz CSV"),
                        "ERROR: line 1: expected 4 fields, got 1")
                .expect("JSONL: komentarz w stylu CSV to błąd", jsonl("# to nie jest komentarz JSONL"),
                        "ERROR: line 1: not a JSON object")
                .expect("JSONL: brak pola", jsonl("{\"title\": \"Diuna\", \"format\": \"IMAX\", \"hall\": \"1\"}"),
                        "ERROR: line 1: missing field: start")
                .expect("numer wiersza liczy też komentarze i puste linie",
                        csv("# a", "", "Diuna;4DX;1;20:30"),
                        "ERROR: line 3: unknown format: 4DX")
                .expect("zła godzina", jsonl("{\"title\": \"Diuna\", \"format\": \"IMAX\", \"hall\": \"1\", \"start\": \"25:00\"}"),
                        "ERROR: line 1: bad start time: 25:00")
                .expect("pusty tytuł", csv(";2D;1;10:00"), "ERROR: line 1: empty title")
                .expect("pusta sala", jsonl("{\"title\": \"Amator\", \"format\": \"2D\", \"hall\": \"\", \"start\": \"10:00\"}"),
                        "ERROR: line 1: empty hall")
                .expect("walidacja zatrzymuje się na pierwszym złym wierszu",
                        csv("Diuna;IMAX;1;20:30", "Amator;2D;;10:00", "Kraina Lodu;5D;2;10:00"),
                        "ERROR: line 2: empty hall")
                .expect("nieobsługiwane rozszerzenie", new ImportRequest("repertuar.xml", List.of("<r/>")),
                        "ERROR: unsupported file: repertuar.xml")
                .tests();
    }

    private static ImportRequest csv(String... lines) {
        return new ImportRequest("repertuar.csv", List.of(lines));
    }

    private static ImportRequest jsonl(String... lines) {
        return new ImportRequest("repertuar.jsonl", List.of(lines));
    }

    static String run(Supplier<String> report) {
        try {
            return report.get();
        } catch (IllegalArgumentException exception) {
            return "ERROR: " + exception.getMessage();
        }
    }
}
