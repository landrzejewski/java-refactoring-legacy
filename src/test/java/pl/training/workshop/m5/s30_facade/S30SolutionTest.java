package pl.training.workshop.m5.s30_facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m5.s30_facade.step3.BookingFacade;
import pl.training.workshop.m5.s30_facade.step3.BoxOffice;
import pl.training.workshop.m5.s30_facade.step3.Cinema;
import pl.training.workshop.m5.s30_facade.step3.OnlineShop;

/** Krok 3: podsystem schowany, kanały znają tylko fasadę, niezmiennik trzyma się także przy awarii. */
final class S30SolutionTest {
    private static final String STEP3 = "pl.training.workshop.m5.s30_facade.step3.";
    private static final TicketOrder TIMEOUT =
            new TicketOrder("S1", List.of("A1"), "N", "TIMEOUT", "anna@example.com");

    @Test
    void subsystemIsHiddenBehindTheFacade() throws ClassNotFoundException {
        for (String name : List.of("SeatRegistry", "PriceList", "PaymentClient", "TicketMailer")) {
            assertFalse(Modifier.isPublic(Class.forName(STEP3 + name).getModifiers()), name);
        }
        assertEquals(0, BookingFacade.class.getConstructors().length, "fasadę składa korzeń kompozycji");
    }

    @Test
    void salesChannelsKnowOnlyTheFacade() {
        for (Class<?> channel : List.of(OnlineShop.class, BoxOffice.class)) {
            List<Class<?>> dependencies = Arrays.stream(channel.getDeclaredFields())
                    .filter(field -> !Modifier.isStatic(field.getModifiers()))
                    .<Class<?>>map(Field::getType)
                    .toList();
            assertEquals(List.of(BookingFacade.class), dependencies, channel.getSimpleName());
        }
    }

    @Test
    void gatewayFailureNoLongerLeavesSeatsTaken() {
        Cinema cinema = new Cinema();
        assertThrows(IllegalStateException.class, () -> cinema.onlineShop().buy(TIMEOUT));
        assertEquals("miejsca={S1=[]} platnosci=[] maile=[]", cinema.state());
    }

    @Test
    void beforeStep3AGatewayFailureLeakedSeats() {
        var cinema = new pl.training.workshop.m5.s30_facade.step2.Cinema();
        assertThrows(IllegalStateException.class, () -> cinema.onlineShop().buy(TIMEOUT));
        assertEquals("miejsca={S1=[A1]} platnosci=[] maile=[]", cinema.state());
    }
}
