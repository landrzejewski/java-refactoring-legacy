package pl.training.workshop.m5.s09_overloading;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.workshop.shared.Money;

/** Overloading (wybór statyczny) kontra overriding (dyspozycja dynamiczna) - wynik każdego wariantu. */
final class S09SolutionTest {
    private static final Money IMAX = Money.of("40.00");
    private static final Money STANDARD_2D = Money.of("25.00");

    @Test
    void startPicksOverloadByDeclaredType() {
        pl.training.workshop.m5.s09_overloading.start.Ticket declaredAsBase = new pl.training.workshop.m5.s09_overloading.start.StudentTicket("Amator", STANDARD_2D);
        assertEquals(Money.of("25.00"), new pl.training.workshop.m5.s09_overloading.start.PriceList().price(declaredAsBase), "pułapka: price(Ticket)");
        List<pl.training.workshop.m5.s09_overloading.start.Ticket> cart = List.of(
                new pl.training.workshop.m5.s09_overloading.start.Ticket("Diuna", IMAX), new pl.training.workshop.m5.s09_overloading.start.StudentTicket("Amator", STANDARD_2D));
        assertEquals(Money.of("65.00"), new pl.training.workshop.m5.s09_overloading.start.Checkout().total(cart), "student zapłacił pełną cenę");
    }

    @Test
    void overridingDispatchesOnRuntimeClass() {
        List<pl.training.workshop.m5.s09_overloading.step1.Ticket> cart = List.of(
                new pl.training.workshop.m5.s09_overloading.step1.Ticket("Diuna", IMAX), new pl.training.workshop.m5.s09_overloading.step1.StudentTicket("Amator", STANDARD_2D));
        assertEquals(Money.of("58.75"), new pl.training.workshop.m5.s09_overloading.step1.Checkout().total(cart));
    }

    @Test
    void overloadedEqualsIsInvisibleToCollections() {
        List<pl.training.workshop.m5.s09_overloading.start.Ticket> cart = List.of(new pl.training.workshop.m5.s09_overloading.start.Ticket("Diuna", IMAX));
        pl.training.workshop.m5.s09_overloading.start.Ticket same = new pl.training.workshop.m5.s09_overloading.start.Ticket("Diuna", IMAX);
        assertTrue(cart.get(0).equals(same), "wywołanie z typem Ticket wybiera przeciążenie");
        assertFalse(new pl.training.workshop.m5.s09_overloading.start.Checkout().alreadyInCart(cart, same), "pułapka: contains() woła equals(Object)");
        assertFalse(new pl.training.workshop.m5.s09_overloading.step1.Checkout().alreadyInCart(
                List.of(new pl.training.workshop.m5.s09_overloading.step1.Ticket("Diuna", IMAX)), new pl.training.workshop.m5.s09_overloading.step1.Ticket("Diuna", IMAX)));
    }

    @Test
    void solutionOverridesEqualsAndHashCode() {
        List<pl.training.workshop.m5.s09_overloading.step2.Ticket> cart = List.of(new pl.training.workshop.m5.s09_overloading.step2.Ticket("Diuna", IMAX));
        assertTrue(new pl.training.workshop.m5.s09_overloading.step2.Checkout().alreadyInCart(cart, new pl.training.workshop.m5.s09_overloading.step2.Ticket("Diuna", IMAX)));
        assertFalse(new pl.training.workshop.m5.s09_overloading.step2.Checkout().alreadyInCart(cart, new pl.training.workshop.m5.s09_overloading.step2.StudentTicket("Diuna", IMAX)));
        assertEquals(new pl.training.workshop.m5.s09_overloading.step2.Ticket("Diuna", IMAX).hashCode(), new pl.training.workshop.m5.s09_overloading.step2.Ticket("Diuna", IMAX).hashCode());
    }
}
