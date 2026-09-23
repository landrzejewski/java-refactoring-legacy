package pl.training.workshop.m6.s07_decorator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m6.s07_decorator.step3.Insurance;
import pl.training.workshop.m6.s07_decorator.step3.PricedTicket;
import pl.training.workshop.m6.s07_decorator.step3.Ticket;
import pl.training.workshop.m6.s07_decorator.step3.TicketAssembler;
import pl.training.workshop.m6.s07_decorator.step3.VipSeat;
import pl.training.workshop.shared.Money;

/** Granice przezroczystości dekoratora: instanceof, equals, kolejność. */
final class S07SolutionTest {
    private final Ticket core = new Ticket("Diuna", "IMAX", Money.of("40.00"));

    @Test
    void instanceofSeesOnlyTheOutermostDecorator() {
        PricedTicket ticket = new TicketAssembler().assemble(
                new TicketOrder("Diuna", "IMAX", Money.of("40.00"), true, false, true));
        assertTrue(ticket instanceof Insurance);
        assertFalse(ticket instanceof VipSeat, "VIP jest ukryty wewnątrz - pytanie 'czy VIP?' wymaga innego API");
    }

    @Test
    void decoratedTicketIsNotEqualToItsCoreDespiteSameTitle() {
        assertNotEquals(core, new VipSeat(core));
    }

    @Test
    void orderOfDecoratorsChangesDescriptionButNotPrice() {
        PricedTicket a = new Insurance(new VipSeat(core));
        PricedTicket b = new VipSeat(new Insurance(core));
        assertEquals(a.price(), b.price());
        assertNotEquals(a.description(), b.description());
        assertNotEquals(a, b);
    }
}
