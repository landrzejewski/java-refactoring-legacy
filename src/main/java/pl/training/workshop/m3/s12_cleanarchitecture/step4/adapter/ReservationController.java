package pl.training.workshop.m3.s12_cleanarchitecture.step4.adapter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import pl.training.workshop.m3.s12_cleanarchitecture.step4.app.BookSeats;
import pl.training.workshop.m3.s12_cleanarchitecture.step4.app.BookSeatsCommand;
import pl.training.workshop.m3.s12_cleanarchitecture.step4.app.Booking;

/**
 * Krok 4: adapter wejściowy dostaje gotowy przypadek użycia - nie wie, jakie
 * adaptery wyjściowe stoją za portami. Tylko tłumaczy żądanie i odpowiedź.
 */
public final class ReservationController {
    private final BookSeats bookSeats;

    public ReservationController(BookSeats bookSeats) {
        this.bookSeats = bookSeats;
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
