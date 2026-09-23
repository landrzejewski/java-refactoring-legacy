package pl.training.workshop.m3.s12_cleanarchitecture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m3.s12_cleanarchitecture.step4.app.BookSeats;
import pl.training.workshop.m3.s12_cleanarchitecture.step4.app.BookSeatsCommand;
import pl.training.workshop.m3.s12_cleanarchitecture.step4.app.Booking;

/**
 * Test przypadku użycia z kroku 4 na adapterach w pamięci (lambdy portów):
 * bez HTTP, bez RowStore, bez Outbox. Sprawdza regułę ceny i protokół efektów.
 */
final class S12UseCaseTest {
    private final List<String> saved = new ArrayList<>();
    private final List<String> notified = new ArrayList<>();

    @Test
    void savesThenNotifies() {
        BookSeats useCase = new BookSeats(
                reservation -> {
                    saved.add(reservation.email() + " " + reservation.total());
                    return "X-7";
                },
                (id, reservation) -> notified.add(id));

        Booking booking = useCase.execute(new BookSeatsCommand("anna@kino.pl", "3D", List.of(2, 11)));

        assertEquals(new Booking("X-7", new BigDecimal("74.00")), booking);
        assertEquals(List.of("anna@kino.pl 74.00"), saved);
        assertEquals(List.of("X-7"), notified);
    }

    @Test
    void doesNotNotifyWhenSavingFails() {
        BookSeats useCase = new BookSeats(
                reservation -> {
                    throw new IllegalStateException("zapis nieudany");
                },
                (id, reservation) -> notified.add(id));

        assertThrows(IllegalStateException.class,
                () -> useCase.execute(new BookSeatsCommand("jan@kino.pl", "2D", List.of(1))));
        assertEquals(List.of(), notified);
    }
}
