package pl.training.workshop.m7.s14_contractchange;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * Część przypadków nie zmienia się nigdy. Dwa przypadki brzegowe pokazują, który krok
 * zmienił kontrakt: krok 2 zmienił format i zaokrąglenie, krok 3 świadomie tylko zaokrąglenie.
 */
final class S14ContractTest {
    record Cancel(double ticketsPaid, long minutesBeforeStart) {
    }

    private static final Cancel HALF_CENT = new Cancel(64.35, 120);
    private static final Cancel AFTER_START = new Cancel(114.00, -10);

    @TestFactory
    Stream<DynamicTest> ordinaryRefundsNeverChange() {
        return Scene.<Cancel, String>variants()
                .variant("start", S14ContractTest::start)
                .variant("step1", S14ContractTest::step1)
                .variant("step2", S14ContractTest::step2)
                .variant("step3", S14ContractTest::step3)
                .expect(">= 24h: 100% - 3.00", new Cancel(114.00, 48 * 60), "111.00")
                .expect("dokladnie 24h", new Cancel(114.00, 24 * 60), "111.00")
                .expect("< 24h: 50% - 3.00", new Cancel(104.40, 10 * 60), "49.20")
                .expect("< 24h, kwota z groszami", new Cancel(153.00, 30), "73.50")
                .tests();
    }

    @Test
    void refactoringStepKeepsHistoricalRoundingAndFormat() {
        assertEquals("29.17", start(HALF_CENT));
        assertEquals("29.17", step1(HALF_CENT));
        assertEquals("0.00", start(AFTER_START));
        assertEquals("0.00", step1(AFTER_START));
    }

    @Test
    void byTheWayStepChangedTwoThingsAtOnce() {
        assertEquals("29.18", step2(HALF_CENT), "zaokraglenie: HALF_UP na dokladnej wartosci 29.175");
        assertEquals("0", step2(AFTER_START), "format: BigDecimal.ZERO zamiast 0.00");
    }

    @Test
    void deliberateStepChangesOnlyTheApprovedRounding() {
        assertEquals("29.18", step3(HALF_CENT), "zatwierdzona zmiana kontraktu");
        assertEquals("0.00", step3(AFTER_START), "format przywrocony");
    }

    private static String start(Cancel c) {
        return new pl.training.workshop.m7.s14_contractchange.start.RefundCalculator()
                .refund(c.ticketsPaid(), c.minutesBeforeStart());
    }

    private static String step1(Cancel c) {
        return new pl.training.workshop.m7.s14_contractchange.step1.RefundCalculator()
                .refund(c.ticketsPaid(), c.minutesBeforeStart());
    }

    private static String step2(Cancel c) {
        return new pl.training.workshop.m7.s14_contractchange.step2.RefundCalculator()
                .refund(c.ticketsPaid(), c.minutesBeforeStart());
    }

    private static String step3(Cancel c) {
        return new pl.training.workshop.m7.s14_contractchange.step3.RefundCalculator()
                .refund(c.ticketsPaid(), c.minutesBeforeStart());
    }
}
