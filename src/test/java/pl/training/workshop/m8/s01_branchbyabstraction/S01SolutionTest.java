package pl.training.workshop.m8.s01_branchbyabstraction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m8.s01_branchbyabstraction.step3.LegacyTicketPricing;
import pl.training.workshop.m8.s01_branchbyabstraction.step3.ModernTicketPricing;
import pl.training.workshop.m8.s01_branchbyabstraction.step3.TicketPricing;
import pl.training.workshop.shared.Money;

/** Wspólny test kontraktowy obu implementacji za abstrakcją i dowód zamknięcia migracji. */
final class S01SolutionTest {
    private final List<BookingRequest> requests = List.of(
            new BookingRequest(S01EquivalenceTest.DIUNA, List.of("A5", "A10"), List.of("N", "S"), true, false),
            new BookingRequest(S01EquivalenceTest.KRAINA_LODU, List.of("B1", "B2"), List.of("C", "N"), false, false),
            new BookingRequest(S01EquivalenceTest.AMATOR, S01EquivalenceTest.GROUP_SEATS,
                    Collections.nCopies(10, "S"), false, false),
            new BookingRequest(S01EquivalenceTest.AMATOR_RANO, List.of("C12", "C13"), List.of("E", "C"), true, false));

    @Test
    void legacyAndModernPricingFulfilTheSameContract() {
        TicketPricing legacy = new LegacyTicketPricing();
        TicketPricing modern = new ModernTicketPricing();
        for (BookingRequest request : requests) {
            assertEquals(legacy.total(request), modern.total(request), request.toString());
        }
    }

    @Test
    void modernPricingUsesMoneyWithScaleTwo() {
        Money total = new ModernTicketPricing().total(requests.get(2));
        // 10 x (25 - 25%) = 187.50, grupa -10% = 168.75
        assertEquals(Money.of("168.75"), total);
    }

    @Test
    void migrationIsClosedOnlyWhenTheOldPathIsGone() {
        String step4 = "pl.training.workshop.m8.s01_branchbyabstraction.step4.";
        assertThrows(ClassNotFoundException.class, () -> Class.forName(step4 + "LegacyTicketPricing"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName(step4 + "PricingMode"));
    }
}
