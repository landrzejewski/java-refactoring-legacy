package pl.training.workshop.m7.s04_removeduplication;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Dokumentuje różnicę ukrytą w duplikacie: 3 x student 2D (18.75) + 7 x normalny (25.00)
 * = 231.25, rabat 23.125. Kasa (HALF_UP) odejmuje 23.13, sklep (HALF_EVEN) 23.12.
 * Ujednolicenie trybu to zmiana kontraktu sklepu - osobny, świadomy krok 2.
 */
final class S04RoundingDecisionTest {
    static final List<BigDecimal> EDGE = edge();

    @Test
    void startAndStep1KeepTheHistoricalWebRounding() {
        assertEquals("228.13",
                new pl.training.workshop.m7.s04_removeduplication.start.WebShop().total(EDGE).toPlainString());
        assertEquals("228.13",
                new pl.training.workshop.m7.s04_removeduplication.step1.WebShop().total(EDGE).toPlainString());
    }

    @Test
    void step2DeliberatelyAlignsWebWithBoxOffice() {
        assertEquals("228.12",
                new pl.training.workshop.m7.s04_removeduplication.step2.WebShop().total(EDGE).toPlainString());
        assertEquals("228.12",
                new pl.training.workshop.m7.s04_removeduplication.step3.WebShop().total(EDGE).toPlainString());
    }

    private static List<BigDecimal> edge() {
        List<BigDecimal> prices = S04EquivalenceTest.prices("18.75", 3);
        prices.addAll(S04EquivalenceTest.prices("25.00", 7));
        return List.copyOf(prices);
    }
}
