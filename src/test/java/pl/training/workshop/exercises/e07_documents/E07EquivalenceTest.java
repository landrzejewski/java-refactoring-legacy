package pl.training.workshop.exercises.e07_documents;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/**
 * Dokument sprzedaży znak po znaku: każdy kanał, każda sekcja osobno i razem (kolejność sekcji
 * jest kontraktem), paragon bez baru, próg 100 punktów oraz błędy walidacji.
 */
final class E07EquivalenceTest {
    private static final Sale DIUNA = new Sale("S-1001", "Diuna", "R10-5", Money.of("40.00"), Money.of("12.00"), 96);
    private static final Sale AMATOR = new Sale("S-1002", "Amator", "R3-7", Money.of("25.00"), Money.ZERO, 10);

    @TestFactory
    Stream<DynamicTest> everyStepPrintsTheSameDocument() {
        return Scene.<PrintRequest, String>variants()
                .variant("start", r -> run(() -> new pl.training.workshop.exercises.e07_documents.start.SalesDocuments()
                        .print(r.sale(), r.channel(), r.options())))
                .variant("step1", r -> run(() -> new pl.training.workshop.exercises.e07_documents.step1.SalesDocuments()
                        .print(r.sale(), r.channel(), r.options())))
                .variant("step2", r -> run(() -> new pl.training.workshop.exercises.e07_documents.step2.SalesDocuments()
                        .print(r.sale(), r.channel(), r.options())))
                .expect("KIOSK bez opcji", new PrintRequest(DIUNA, Channel.KIOSK, PrintOptions.NONE), """
                        ********************************
                        BILET S-1001
                        Diuna
                        Miejsce R10-5
                        || ||| | S-1001 | ||| ||
                        PARAGON
                        Bilet                      40.00
                        Bar                        12.00
                        RAZEM                      52.00
                        ********************************""")
                .expect("MOBILE bez opcji", new PrintRequest(DIUNA, Channel.MOBILE, PrintOptions.NONE), """
                        <ticket><h1>Diuna</h1><p>Bilet S-1001</p><p>Miejsce R10-5</p></ticket>
                        <qr>S-1001</qr>
                        <receipt><li>Bilet 40.00</li><li>Bar 12.00</li><li><b>Razem 52.00</b></li></receipt>""")
                .expect("BOX_OFFICE bez opcji", new PrintRequest(DIUNA, Channel.BOX_OFFICE, PrintOptions.NONE), """
                        CINELEGACY - BILET WSTEPU
                        Numer:   S-1001
                        Film:    Diuna
                        Miejsce: R10-5
                        Kod kontrolny: #S-1001#
                        PARAGON FISKALNY
                        Bilet ............. 40.00
                        Bar ............... 12.00
                        RAZEM ............. 52.00""")
                .expect("KIOSK wszystkie sekcje, bez baru", new PrintRequest(AMATOR, Channel.KIOSK, PrintOptions.ALL), """
                        ********************************
                        BILET S-1002
                        Amator
                        Miejsce R3-7
                        || ||| | S-1002 | ||| ||
                        PARAGON
                        Bilet                      25.00
                        RAZEM                      25.00
                        ********************************
                        VAT 8% bilet: 1.85
                        --------------------------------
                        Kod KINO10: -10% na nastepny bilet online
                        --------------------------------
                        Punkty: 10 + 2 = 12
                        --------------------------------""")
                .expect("MOBILE tylko VAT, z barem",
                        new PrintRequest(DIUNA, Channel.MOBILE, new PrintOptions(true, false, false)), """
                        <ticket><h1>Diuna</h1><p>Bilet S-1001</p><p>Miejsce R10-5</p></ticket>
                        <qr>S-1001</qr>
                        <receipt><li>Bilet 40.00</li><li>Bar 12.00</li><li><b>Razem 52.00</b></li></receipt>
                        <section><p>VAT 8% bilet: 2.96</p><p>VAT 23% bar: 2.24</p></section>""")
                .expect("BOX_OFFICE promocja i stopka, prog 100 punktow",
                        new PrintRequest(DIUNA, Channel.BOX_OFFICE, new PrintOptions(false, true, true)), """
                        CINELEGACY - BILET WSTEPU
                        Numer:   S-1001
                        Film:    Diuna
                        Miejsce: R10-5
                        Kod kontrolny: #S-1001#
                        PARAGON FISKALNY
                        Bilet ............. 40.00
                        Bar ............... 12.00
                        RAZEM ............. 52.00
                          Kod KINO10: -10% na nastepny bilet online
                          Punkty: 96 + 4 = 100
                          Odbierz darmowy bilet 2D""")
                .expect("KIOSK tylko stopka, ponizej progu",
                        new PrintRequest(AMATOR, Channel.KIOSK, new PrintOptions(false, false, true)), """
                        ********************************
                        BILET S-1002
                        Amator
                        Miejsce R3-7
                        || ||| | S-1002 | ||| ||
                        PARAGON
                        Bilet                      25.00
                        RAZEM                      25.00
                        ********************************
                        Punkty: 10 + 2 = 12
                        --------------------------------""")
                .expect("MOBILE wszystkie sekcje", new PrintRequest(DIUNA, Channel.MOBILE, PrintOptions.ALL), """
                        <ticket><h1>Diuna</h1><p>Bilet S-1001</p><p>Miejsce R10-5</p></ticket>
                        <qr>S-1001</qr>
                        <receipt><li>Bilet 40.00</li><li>Bar 12.00</li><li><b>Razem 52.00</b></li></receipt>
                        <section><p>VAT 8% bilet: 2.96</p><p>VAT 23% bar: 2.24</p></section>
                        <section><p>Kod KINO10: -10% na nastepny bilet online</p></section>
                        <section><p>Punkty: 96 + 4 = 100</p><p>Odbierz darmowy bilet 2D</p></section>""")
                .expect("BOX_OFFICE wszystkie sekcje, bez baru",
                        new PrintRequest(AMATOR, Channel.BOX_OFFICE, PrintOptions.ALL), """
                        CINELEGACY - BILET WSTEPU
                        Numer:   S-1002
                        Film:    Amator
                        Miejsce: R3-7
                        Kod kontrolny: #S-1002#
                        PARAGON FISKALNY
                        Bilet ............. 25.00
                        RAZEM ............. 25.00
                          VAT 8% bilet: 1.85
                          Kod KINO10: -10% na nastepny bilet online
                          Punkty: 10 + 2 = 12""")
                .expect("sprzedaz null", new PrintRequest(null, Channel.KIOSK, PrintOptions.NONE),
                        "ERROR: sale must not be null")
                .expect("kanal null", new PrintRequest(DIUNA, null, PrintOptions.NONE),
                        "ERROR: channel must not be null")
                .expect("opcje null", new PrintRequest(DIUNA, Channel.MOBILE, null),
                        "ERROR: options must not be null")
                .expect("ujemna cena biletu",
                        new PrintRequest(new Sale("S-1", "Diuna", "R1-1", Money.of("-1.00"), Money.ZERO, 0),
                                Channel.BOX_OFFICE, PrintOptions.ALL),
                        "ERROR: ticket price must not be negative")
                .expect("ujemna kwota baru",
                        new PrintRequest(new Sale("S-1", "Diuna", "R1-1", Money.of("25.00"), Money.of("-0.01"), 0),
                                Channel.KIOSK, PrintOptions.NONE),
                        "ERROR: bar price must not be negative")
                .tests();
    }

    static String run(Supplier<String> document) {
        try {
            return document.get();
        } catch (IllegalArgumentException exception) {
            return "ERROR: " + exception.getMessage();
        }
    }
}
