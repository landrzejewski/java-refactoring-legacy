package pl.training.workshop.m5.s26_variability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m5.s26_variability.step3.BookCommand;
import pl.training.workshop.m5.s26_variability.step3.Booking;
import pl.training.workshop.m5.s26_variability.step3.BookingLedger;
import pl.training.workshop.m5.s26_variability.step3.BoxOfficeDesk;
import pl.training.workshop.m5.s26_variability.step3.CancelCommand;
import pl.training.workshop.m5.s26_variability.step3.DeskCommand;
import pl.training.workshop.m5.s26_variability.step3.PayCommand;
import pl.training.workshop.m5.s26_variability.step3.TicketPricing;
import pl.training.workshop.m5.s26_variability.step3.TicketPricings;
import pl.training.workshop.m5.s26_variability.step3.UseCommand;
import pl.training.workshop.shared.Money;

/** Trzy mechanizmy istnieją i każdy obsługuje jedną oś zmienności - osie się nie mieszają. */
final class S26SolutionTest {
    private static final List<Class<?>> COMMANDS =
            List.of(BookCommand.class, PayCommand.class, UseCommand.class, CancelCommand.class);

    @Test
    void ticketPriceIsAStatelessStrategyThatKnowsOnlyMoney() throws NoSuchMethodException {
        assertTrue(TicketPricing.class.isAnnotationPresent(FunctionalInterface.class));
        Method price = TicketPricing.class.getMethod("price", Money.class);
        assertEquals(Money.class, price.getReturnType());
        assertSame(TicketPricings.forType("S"), TicketPricings.forType("S"));
        assertEquals(Money.of("30.00"), TicketPricings.forType("S").price(Money.of("40.00")));
    }

    @Test
    void everyOperatorOperationIsACommandWithUndo() throws NoSuchMethodException {
        for (Class<?> command : COMMANDS) {
            assertTrue(DeskCommand.class.isAssignableFrom(command), command.getSimpleName());
        }
        DeskCommand.class.getMethod("undo", BookingLedger.class);
        DeskCommand.class.getMethod("journalEntry");
    }

    @Test
    void lifecycleDecidesWhatCancelMeans() {
        Booking unpaid = new Booking(Money.of("40.00"));
        assertEquals(Money.ZERO, unpaid.cancel());

        Booking paid = new Booking(Money.of("40.00"));
        assertEquals(Money.of("40.00"), paid.pay());
        assertEquals(Money.of("40.00"), paid.cancel(), "w PAID anulowanie oddaje pieniądze");
        assertEquals(Status.CANCELLED, paid.status());
        var error = assertThrows(IllegalStateException.class, paid::use);
        assertEquals("cannot use in CANCELLED", error.getMessage());
    }

    @Test
    void stateLivesInsideBookingAsPrivateType() {
        Set<Class<?>> nested = Set.of(Booking.class.getDeclaredClasses());
        assertTrue(nested.stream().anyMatch(type -> type.isEnum() && Modifier.isPrivate(type.getModifiers())),
                "stany jako prywatny typ rezerwacji");
    }

    @Test
    void axesAreNotMixed() {
        assertFalse(fieldTypes(Booking.class).contains(TicketPricing.class), "stan nie zna cennika");
        assertFalse(fieldTypes(Booking.class).contains(String.class), "stan nie zna nazw operacji");
        for (Class<?> command : COMMANDS) {
            Set<Class<?>> types = fieldTypes(command);
            assertFalse(types.contains(TicketPricing.class), command.getSimpleName() + " nie trzyma strategii");
            assertFalse(types.contains(Booking.class), command.getSimpleName() + " nie sięga do stanu rezerwacji");
        }
        Set<Class<?>> desk = fieldTypes(BoxOfficeDesk.class);
        assertFalse(desk.contains(Money.class) || desk.contains(Status.class) || desk.contains(Booking.class),
                "pulpit zna tylko polecenia i odbiorcę: " + desk);
    }

    private static Set<Class<?>> fieldTypes(Class<?> type) {
        return Arrays.stream(type.getDeclaredFields())
                .filter(field -> !field.isSynthetic())
                .map(Field::getType)
                .collect(Collectors.toSet());
    }
}
