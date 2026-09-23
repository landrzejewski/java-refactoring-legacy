package pl.training.workshop.m8.s06_reviewableseries;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/** Test równoważności dla commitów refaktoryzacyjnych (1 i 2). Commit 3 zmienia zachowanie - osobny test. */
final class S06EquivalenceTest {
    static final LocalDateTime MONDAY_EVENING = LocalDateTime.of(2026, 3, 9, 18, 0);
    static final LocalDateTime TUESDAY_EVENING = LocalDateTime.of(2026, 3, 10, 18, 0);
    static final LocalDateTime TUESDAY_MORNING = LocalDateTime.of(2026, 3, 10, 10, 0);

    @TestFactory
    Stream<DynamicTest> refactoringCommitsDoNotChangePrices() {
        return Scene.<TicketQuery, Money>variants()
                .variant("start", new pl.training.workshop.m8.s06_reviewableseries.start.PriceList()::price)
                .variant("step1", new pl.training.workshop.m8.s06_reviewableseries.step1.PriceList()::price)
                .variant("step2", new pl.training.workshop.m8.s06_reviewableseries.step2.PriceList()::price)
                .expect("2D normalny, poniedziałek", new TicketQuery("2D", "NORMAL", MONDAY_EVENING, 5), Money.of("25.00"))
                .expect("2D normalny, wtorek", new TicketQuery("2D", "NORMAL", TUESDAY_EVENING, 5), Money.of("25.00"))
                .expect("IMAX student VIP", new TicketQuery("IMAX", "STUDENT", MONDAY_EVENING, 10), Money.of("40.00"))
                .expect("3D dziecko rano", new TicketQuery("3D", "CHILD", TUESDAY_MORNING, 3), Money.of("14.20"))
                .expect("2D senior", new TicketQuery("2D", "SENIOR", TUESDAY_EVENING, 1), Money.of("17.50"))
                .tests();
    }
}
