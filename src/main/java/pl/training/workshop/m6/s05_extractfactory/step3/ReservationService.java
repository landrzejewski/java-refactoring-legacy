package pl.training.workshop.m6.s05_extractfactory.step3;

import java.time.Clock;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import pl.training.workshop.m6.s05_extractfactory.Reservation;

/**
 * Krok 3: fabryka wstrzyknięta przez konstruktor. Test może podać fabrykę z innym zegarem,
 * a numeracja może być współdzielona przez wiele serwisów.
 */
public final class ReservationService {
    private final ReservationFactory factory;
    private final Set<String> takenSeats = new HashSet<>();

    /** Dotychczasowy konstruktor zostaje jako wygodny skrót - klienci nie muszą się zmieniać. */
    public ReservationService(Clock clock) {
        this(new ReservationFactory(clock));
    }

    public ReservationService(ReservationFactory factory) {
        this.factory = Objects.requireNonNull(factory, "factory");
    }

    public Reservation reserve(String channel, String email, List<String> seats) {
        requireFree(seats);
        Reservation reservation = factory.create(channel, email, seats);
        takenSeats.addAll(seats);
        return reservation;
    }

    public Reservation reserveGroup(String email, List<String> seats) {
        if (seats.size() < 10) {
            throw new IllegalArgumentException("group needs 10+ seats");
        }
        requireFree(seats);
        Reservation reservation = factory.create("ONLINE", email, seats);
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
}
