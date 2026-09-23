package pl.training.workshop.m6.s01_strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m6.s01_strategy.step3.DiscountPrograms;
import pl.training.workshop.m6.s01_strategy.step3.TicketPricer;
import pl.training.workshop.shared.Money;

/** Kontrakt rozwiązania i pułapka "momentu wyboru" strategii. */
final class S01SolutionTest {
    @Test
    void strategiesAreSharedBecauseTheyAreStateless() {
        assertSame(DiscountPrograms.forName("STANDARD"), DiscountPrograms.forName("STANDARD"));
    }

    @Test
    void newProgramIsJustAnotherStrategyWithoutTouchingTheContext() {
        TicketPricer blackFriday = new TicketPricer((base, type) -> base.percent(50));
        assertEquals(Money.of("20.00"), blackFriday.price(Money.of("40.00"), "N"));
    }

    @Test
    void beforeStep3BaseIsValidatedBeforeTheProgram() {
        var step2 = new pl.training.workshop.m6.s01_strategy.step2.TicketPricer();
        var error = assertThrows(IllegalArgumentException.class,
                () -> step2.price(Money.of("-1.00"), "N", "BLACK_FRIDAY"));
        assertEquals("base price must not be negative", error.getMessage());
    }

    @Test
    void choosingInConstructorMovesTheUnknownProgramErrorEarlier() {
        var error = assertThrows(IllegalArgumentException.class,
                () -> new TicketPricer(DiscountPrograms.forName("BLACK_FRIDAY")).price(Money.of("-1.00"), "N"));
        assertEquals("unknown program: BLACK_FRIDAY", error.getMessage());
    }
}
