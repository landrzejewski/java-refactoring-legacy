package pl.training.workshop.m3.s12_cleanarchitecture.step2;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import pl.training.workshop.m3.s12_cleanarchitecture.Outbox;
import pl.training.workshop.m3.s12_cleanarchitecture.RowStore;

/**
 * Adapter wejściowy: tłumaczy parametry na {@link BookSeatsCommand}, wynik i wyjątki
 * na kody odpowiedzi. Na razie składa też graf obiektów - przeniesiemy to w kroku 4.
 */
public final class ReservationController {
    private final BookSeats bookSeats;

    public ReservationController(RowStore db, Outbox outbox) {
        this.bookSeats = new BookSeats(
                new RowStoreReservationStore(db), new OutboxBookingNotifier(outbox));
    }

    public String handle(Map<String, String> params) {
        String email = params.get("email");
        if (email == null || email.isBlank()) {
            return "400 brak email";
        }
        List<Integer> rows = Arrays.stream(params.getOrDefault("rows", "").split(","))
                .filter(s -> !s.isBlank()).map(Integer::parseInt).toList();
        try {
            Booking booking = bookSeats.execute(
                    new BookSeatsCommand(email, params.getOrDefault("format", "2D"), rows));
            return "201 " + booking.id() + " " + booking.total();
        } catch (IllegalArgumentException e) {
            return "400 " + e.getMessage();
        } catch (IllegalStateException e) {
            return "503 " + e.getMessage();
        }
    }
}
