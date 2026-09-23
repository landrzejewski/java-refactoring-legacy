package pl.training.workshop.m3.s07_srp;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Podział raportu według aktorów nie zmienia ani jednego znaku dokumentu. */
final class S07EquivalenceTest {
    private static Sale sale(String title, int tickets, String ticketRevenue, String barRevenue) {
        return new Sale(title, tickets, new BigDecimal(ticketRevenue), new BigDecimal(barRevenue));
    }

    @TestFactory
    Stream<DynamicTest> everyStepRendersTheSameReport() {
        return Scene.<List<Sale>, String>variants()
                .variant("start", new pl.training.workshop.m3.s07_srp.start.DailyReport()::render)
                .variant("step1", new pl.training.workshop.m3.s07_srp.step1.DailyReport()::render)
                .variant("step2", new pl.training.workshop.m3.s07_srp.step2.DailyReport()::render)
                .variant("step3", new pl.training.workshop.m3.s07_srp.step3.DailyReport()::render)
                .expect("trzy seanse, Diuna dwa razy",
                        List.of(sale("Diuna", 2, "80.00", "18.00"),
                                sale("Amator", 3, "75.00", "12.00"),
                                sale("Diuna", 1, "40.00", "0.00")),
                        """
                                KSIEGOWOSC
                                Bilety brutto 195.00, netto 180.56
                                Bar brutto 30.00, netto 24.39
                                Razem brutto 225.00
                                MARKETING
                                Hit dnia: Diuna (138.00)
                                Sprzedanych biletow: 6
                                """)
                .expect("remis - wygrywa pierwszy alfabetycznie",
                        List.of(sale("Kraina Lodu", 1, "20.00", "5.00"), sale("Amator", 1, "25.00", "0.00")),
                        """
                                KSIEGOWOSC
                                Bilety brutto 45.00, netto 41.67
                                Bar brutto 5.00, netto 4.07
                                Razem brutto 50.00
                                MARKETING
                                Hit dnia: Amator (25.00)
                                Sprzedanych biletow: 2
                                """)
                .expect("dzien bez sprzedazy", List.of(),
                        """
                                KSIEGOWOSC
                                Bilety brutto 0.00, netto 0.00
                                Bar brutto 0.00, netto 0.00
                                Razem brutto 0.00
                                MARKETING
                                Hit dnia: brak
                                Sprzedanych biletow: 0
                                """)
                .tests();
    }
}
