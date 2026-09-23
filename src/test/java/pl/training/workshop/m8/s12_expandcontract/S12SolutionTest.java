package pl.training.workshop.m8.s12_expandcontract;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.training.workshop.m8.s12_expandcontract.S12EquivalenceTest.ANNA;
import static pl.training.workshop.m8.s12_expandcontract.S12EquivalenceTest.JAN;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

/** Dane, wycofanie i zamknięcie migracji formatu rezerwacji (expand and contract). */
final class S12SolutionTest {
    @Test
    void step1DualWriteKeepsRollbackToTheOldVersionSafe() {
        BookingTable table = new BookingTable();
        new pl.training.workshop.m8.s12_expandcontract.step1.BookingRepository(table).save(ANNA);
        assertEquals("v2|id=B1|email=anna@kino.pl|seats=A5 A10|total=84.00", table.get("B1").orElseThrow().payload());
        var oldVersion = new pl.training.workshop.m8.s12_expandcontract.start.BookingRepository(table);
        assertEquals(Optional.of(ANNA), oldVersion.find("B1"), "wycofane wydanie czyta dane nowego");
    }

    @Test
    void step2ReadsNewFormatAndFallsBackForOldRows() {
        BookingTable table = new BookingTable();
        new pl.training.workshop.m8.s12_expandcontract.start.BookingRepository(table).save(JAN);
        var repository = new pl.training.workshop.m8.s12_expandcontract.step2.BookingRepository(table);
        repository.save(ANNA);
        assertEquals(Optional.of(JAN), repository.find("B2"), "stary wiersz - odczyt z fallbackiem");
        assertEquals(Optional.of(ANNA), repository.find("B1"));
        assertEquals(Optional.of(ANNA),
                new pl.training.workshop.m8.s12_expandcontract.start.BookingRepository(table).find("B1"));
    }

    @Test
    void step3BackfillIsIdempotentAndPreparesTheContract() {
        BookingTable table = new BookingTable();
        new pl.training.workshop.m8.s12_expandcontract.start.BookingRepository(table).save(JAN);
        var contracted = new pl.training.workshop.m8.s12_expandcontract.step4.BookingRepository(table);
        assertEquals(Optional.empty(), contracted.find("B2"), "contract przed backfillem gubi stare wiersze");

        var repository = new pl.training.workshop.m8.s12_expandcontract.step3.BookingRepository(table);
        assertEquals(1, repository.migrateAll());
        assertEquals(0, repository.migrateAll(), "drugie uruchomienie niczego nie zmienia");
        assertEquals(Optional.of(JAN), contracted.find("B2"));
    }

    @Test
    void step4ClosesTheRollbackWindow() {
        BookingTable table = new BookingTable();
        new pl.training.workshop.m8.s12_expandcontract.step4.BookingRepository(table).save(ANNA);
        assertEquals(Optional.empty(),
                new pl.training.workshop.m8.s12_expandcontract.start.BookingRepository(table).find("B1"),
                "po contract stara wersja nie widzi nowych danych - wycofanie kodu już nie wystarczy");
    }

    @Test
    void payloadFormatIsVersioned() {
        assertThrows(IllegalArgumentException.class, () ->
                pl.training.workshop.m8.s12_expandcontract.step4.BookingPayloadFormat.read("v3|id=B1"));
    }

    @Test
    void step4HasNoReferenceToTheOldFormat() throws IOException {
        Path step4 = Path.of("src/main/java/pl/training/workshop/m8/s12_expandcontract/step4");
        assertTrue(Files.isDirectory(step4), "uruchom testy z katalogu głównego repozytorium, brak " + step4.toAbsolutePath());
        Pattern oldFormat = Pattern.compile("(?i)csv");
        List<String> offenders;
        try (Stream<Path> files = Files.list(step4)) {
            offenders = files.filter(file -> {
                try {
                    return oldFormat.matcher(Files.readString(file)).find();
                } catch (IOException e) {
                    throw new java.io.UncheckedIOException(e);
                }
            }).map(file -> file.getFileName().toString()).toList();
        }
        assertEquals(List.of(), offenders);
        assertThrows(ClassNotFoundException.class,
                () -> Class.forName("pl.training.workshop.m8.s12_expandcontract.step4.CsvBookingFormat"));
    }
}
