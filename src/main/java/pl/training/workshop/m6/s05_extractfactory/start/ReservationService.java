package pl.training.workshop.m6.s05_extractfactory.start;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.training.workshop.m6.s05_extractfactory.Reservation;
import pl.training.workshop.shared.Money;

/**
 * Start: serwis pilnuje zajętości miejsc, ale też wie, jak zbudować rezerwację (numer, opłata,
 * termin ważności). Ta wiedza jest skopiowana w reserve i reserveGroup.
 */
public final class ReservationService {
    private final Clock clock;
    private final Set<String> takenSeats = new HashSet<>();
    private int sequence;

    public ReservationService(Clock clock) {
        this.clock = clock;
    }

    public Reservation reserve(String channel, String email, List<String> seats) {
        for (String seat : seats) {
            if (takenSeats.contains(seat)) {
                throw new IllegalStateException("seat taken: " + seat);
            }
        }
        sequence++;
        String id = "R" + sequence;
        if (!channel.equals("ONLINE") && !channel.equals("BOX_OFFICE")) {
            throw new IllegalArgumentException("unknown channel: " + channel);
        }
        Money fee = channel.equals("ONLINE") ? Money.of("2.00").times(seats.size()) : Money.ZERO;
        LocalDateTime expiresAt = channel.equals("ONLINE")
                ? LocalDateTime.now(clock).plusMinutes(15)
                : null;
        takenSeats.addAll(seats);
        return new Reservation(id, channel, email, List.copyOf(seats), fee, expiresAt);
    }

    public Reservation reserveGroup(String email, List<String> seats) {
        if (seats.size() < 10) {
            throw new IllegalArgumentException("group needs 10+ seats");
        }
        for (String seat : seats) {
            if (takenSeats.contains(seat)) {
                throw new IllegalStateException("seat taken: " + seat);
            }
        }
        sequence++;
        String id = "R" + sequence;
        Money fee = Money.of("2.00").times(seats.size());
        LocalDateTime expiresAt = LocalDateTime.now(clock).plusMinutes(15);
        takenSeats.addAll(seats);
        return new Reservation(id, "ONLINE", email, List.copyOf(seats), fee, expiresAt);
    }
}
