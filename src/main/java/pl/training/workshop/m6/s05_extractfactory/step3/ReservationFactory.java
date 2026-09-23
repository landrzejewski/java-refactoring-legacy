package pl.training.workshop.m6.s05_extractfactory.step3;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import pl.training.workshop.m6.s05_extractfactory.Reservation;
import pl.training.workshop.shared.Money;

/**
 * Krok 3: fabryka bez zmian - teraz składana w korzeniu kompozycji (numeracja,
 * opłata, termin ważności). To zwykła zależność, nie globalny rejestr.
 */
public final class ReservationFactory {
    private final Clock clock;
    private int sequence;

    public ReservationFactory(Clock clock) {
        this.clock = clock;
    }

    public Reservation create(String channel, String email, List<String> seats) {
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
