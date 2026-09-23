package pl.training.workshop.m6.s17_singleton;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m6.s17_singleton.step3.TicketDesk;
import pl.training.workshop.shared.Money;

/** Pomiar przed decyzją o cyklu życia i testowalność po wstrzyknięciu. */
final class S17SolutionTest {
    /** Pomiar w start (licznik instancji); po "warsztat.sh jump" licznika nie ma i test jest pomijany. */
    @Test
    void startCreatesAPriceListForEveryQuote() throws ReflectiveOperationException {
        Method created;
        try {
            created = Class.forName("pl.training.workshop.m6.s17_singleton.start.PriceList").getMethod("created");
        } catch (NoSuchMethodException exception) {
            created = null;
        }
        assumeTrue(created != null, "start nie ma już licznika instancji");
        int before = (int) created.invoke(null);
        var desk = new pl.training.workshop.m6.s17_singleton.start.TicketDesk();
        desk.quote("2D", false);
        desk.quote("3D", false);
        desk.quote("IMAX", true);
        assertEquals(3, (int) created.invoke(null) - before);
    }

    @Test
    void singletonReturnsTheSameInstance() {
        assertSame(pl.training.workshop.m6.s17_singleton.step1.PriceList.getInstance(),
                pl.training.workshop.m6.s17_singleton.step1.PriceList.getInstance());
    }

    @Test
    void injectedTariffNeedsNoGlobalState() {
        TicketDesk promo = new TicketDesk(format -> Money.of("19.00"));
        assertEquals(Money.of("21.00"), promo.quote("IMAX", true));
        assertEquals(Money.of("42.00"), new TicketDesk().quote("IMAX", true), "domyślny cennik bez zmian");
    }
}
