package pl.training.workshop.m6.s05_extractfactory.step1;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.training.workshop.m6.s05_extractfactory.Reservation;
import pl.training.workshop.shared.Money;

/**
 * Krok 1: Extract Method - tworzenie rezerwacji w jednej metodzie newReservation.
 * Kolejność zachowana: numer jest pobierany PRZED walidacją kanału (błąd "spala" numer).
 */
public final class ReservationService {
    private final Clock clock;
    private final Set<String> takenSeats = new HashSet<>();
    private int sequence;

    public ReservationService(Clock clock) {
        this.clock = clock;
    }

    public Reservation reserve(String channel, String email, List<String> seats) {
        requireFree(seats);
        Reservation reservation = newReservation(channel, email, seats);
        takenSeats.addAll(seats);
        return reservation;
    }

    public Reservation reserveGroup(String email, List<String> seats) {
        if (seats.size() < 10) {
            throw new IllegalArgumentException("group needs 10+ seats");
        }
        requireFree(seats);
        Reservation reservation = newReservation("ONLINE", email, seats);
        takenSeats.addAll(seats);
        return reservation;
    }

    private void requireFree(List<String> seats) {
        for (String seat : seats) {
            if (takenSeats.contains(seat)) {
                throw new IllegalStateException("seat taken: " + seat);
            }
        }
    }

    private Reservation newReservation(String channel, String email, List<String> seats) {
        sequence++;
        String id = "R" + sequence;
        if (!channel.equals("ONLINE") && !channel.equals("BOX_OFFICE")) {
            throw new IllegalArgumentException("unknown channel: " + channel);
        }
        Money fee = channel.equals("ONLINE") ? Money.of("2.00").times(seats.size()) : Money.ZERO;
        LocalDateTime expiresAt = channel.equals("ONLINE")
                ? LocalDateTime.now(clock).plusMinutes(15)
                : null;
        return new Reservation(id, channel, email, List.copyOf(seats), fee, expiresAt);
    }
}
