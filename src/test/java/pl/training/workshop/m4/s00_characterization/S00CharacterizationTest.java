package pl.training.workshop.m4.s00_characterization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * Test charakterystyki generatora potwierdzeń - zapisuje, co kod ROBI dziś.
 * Powstaje w trzech ruchach (patrz przewodnik):
 * A) start: "scrubber" maskuje linię z bieżącym czasem, Locale ustawiamy jawnie;
 * B) step1: wstrzyknięty Clock pozwala porównać cały dokument bez maskowania;
 * C) step2: ten sam test chroni pierwszą refaktoryzację.
 * Nazwy przypadków z "ZASTANE" opisują dziwne zachowanie, którego NIE poprawiamy w refaktoryzacji.
 */
final class S00CharacterizationTest {
    private static final Locale SERVER_LOCALE = Locale.forLanguageTag("pl-PL");
    private static final Clock FIXED = Clock.fixed(Instant.parse("2026-09-23T08:15:30Z"), ZoneOffset.UTC);

    private static final Booking IMAX_ONLINE = new Booking("  anna@kino.pl ", "Diuna", 3,
            LocalTime.of(20, 0), List.of("N", "S"), true);
    private static final Booking MORNING_3D = new Booking("jan@kino.pl", "Kraina Lodu", 2,
            LocalTime.of(11, 0), List.of("E", "C"), false);
    private static final Booking TEN_TICKETS = new Booking("jan@kino.pl", "Amator", 1,
            LocalTime.of(18, 30), Collections.nCopies(10, "N"), true);
    private static final Booking ELEVEN_TICKETS = new Booking("jan@kino.pl", "Amator", 1,
            LocalTime.of(18, 30), Collections.nCopies(11, "N"), false);

    @TestFactory
    Stream<DynamicTest> everyVariantPrintsTheApprovedDocument() {
        return Scene.<Booking, String>variants()
                .variant("start", b -> scrubbed(new pl.training.workshop.m4.s00_characterization.start
                        .BookingConfirmation()::confirm, b))
                .variant("step1", b -> scrubbed(new pl.training.workshop.m4.s00_characterization.step1
                        .BookingConfirmation(FIXED)::confirm, b))
                .variant("step2", b -> scrubbed(new pl.training.workshop.m4.s00_characterization.step2
                        .BookingConfirmation(FIXED)::confirm, b))
                .expect("IMAX wieczorem, online, klient z odstępami", IMAX_ONLINE, """
                        POTWIERDZENIE REZERWACJI
                        Klient: ANNA@KINO.PL
                        Film: Diuna, IMAX, 20:00
                        Bilety: 2 [N, S]
                        Bilety razem: 70,00
                        Oplata rezerwacyjna: 4,00
                        Do zaplaty: 74,00
                        Wygenerowano: <czas>
                        """)
                .expect("3D rano, senior i dziecko, kasa", MORNING_3D, """
                        POTWIERDZENIE REZERWACJI
                        Klient: JAN@KINO.PL
                        Film: Kraina Lodu, 3D, 11:00
                        Bilety: 2 [E, C]
                        Bilety razem: 31,60
                        Oplata rezerwacyjna: 0,00
                        Do zaplaty: 31,60
                        Wygenerowano: <czas>
                        """)
                .expect("ZASTANE: 10 biletów bez rabatu grupowego (reguła mówi 10+)", TEN_TICKETS, """
                        POTWIERDZENIE REZERWACJI
                        Klient: JAN@KINO.PL
                        Film: Amator, 2D, 18:30
                        Bilety: 10 [N, N, N, N, N, N, N, N, N, N]
                        Bilety razem: 250,00
                        Oplata rezerwacyjna: 20,00
                        Do zaplaty: 270,00
                        Wygenerowano: <czas>
                        """)
                .expect("11 biletów - rabat grupowy 10%", ELEVEN_TICKETS, """
                        POTWIERDZENIE REZERWACJI
                        Klient: JAN@KINO.PL
                        Film: Amator, 2D, 18:30
                        Bilety: 11 [N, N, N, N, N, N, N, N, N, N, N]
                        Bilety razem: 247,50
                        Oplata rezerwacyjna: 0,00
                        Do zaplaty: 247,50
                        Wygenerowano: <czas>
                        """)
                .tests();
    }

    @Test
    void startPrintsCurrentTimeSoTheTestMustScrubIt() {
        String document = inLocale(SERVER_LOCALE, () -> new pl.training.workshop.m4.s00_characterization
                .start.BookingConfirmation().confirm(IMAX_ONLINE));
        assertTrue(document.matches("(?s).*\nWygenerowano: \\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(:\\d{2})?\n"),
                document);
    }

    @Test
    void fromStep1TheWholeDocumentIsDeterministic() {
        for (Function<Booking, String> variant : List.<Function<Booking, String>>of(
                new pl.training.workshop.m4.s00_characterization.step1.BookingConfirmation(FIXED)::confirm,
                new pl.training.workshop.m4.s00_characterization.step2.BookingConfirmation(FIXED)::confirm)) {
            String document = inLocale(SERVER_LOCALE, () -> variant.apply(IMAX_ONLINE));
            assertTrue(document.endsWith("Do zaplaty: 74,00\nWygenerowano: 2026-09-23T08:15:30\n"), document);
        }
    }

    @Test
    void foundDuringCharacterizationAmountsDependOnServerLocale() {
        String document = inLocale(Locale.US, () -> new pl.training.workshop.m4.s00_characterization
                .step2.BookingConfirmation(FIXED).confirm(IMAX_ONLINE));
        assertTrue(document.contains("Do zaplaty: 74.00\n"), document);
    }

    private static String scrubbed(Function<Booking, String> variant, Booking booking) {
        String document = inLocale(SERVER_LOCALE, () -> variant.apply(booking));
        return document.replaceAll("Wygenerowano: .*\n", "Wygenerowano: <czas>\n");
    }

    /** Ustawia domyślne Locale tylko na czas wywołania - test nie zależy od maszyny. */
    private static String inLocale(Locale locale, Supplier<String> action) {
        Locale previous = Locale.getDefault();
        Locale previousFormat = Locale.getDefault(Locale.Category.FORMAT);
        Locale previousDisplay = Locale.getDefault(Locale.Category.DISPLAY);
        Locale.setDefault(locale);
        try {
            return action.get();
        } finally {
            Locale.setDefault(previous);
            Locale.setDefault(Locale.Category.FORMAT, previousFormat);
            Locale.setDefault(Locale.Category.DISPLAY, previousDisplay);
        }
    }
}
