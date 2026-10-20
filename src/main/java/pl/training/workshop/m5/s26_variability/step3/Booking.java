package pl.training.workshop.m5.s26_variability.step3;

import java.util.Objects;

import pl.training.workshop.m5.s26_variability.Status;
import pl.training.workshop.shared.Money;

/**
 * Krok 3: Replace State-Altering Conditionals with State - cykl życia rezerwacji.
 * Każdy stan zna tylko swoje dozwolone przejścia; domyślnie przejście rzuca wyjątek.
 * CANCEL w NEW zwalnia rezerwację, w PAID oddaje pieniądze - ta sama operacja, inne zachowanie.
 * Stan nie wie, kto o operację poprosił (Command) ani jak liczono cenę (Strategy).
 */
public final class Booking {
    private final Money amount;
    private State state = State.NEW;

    public Booking(Money amount) {
        this.amount = Objects.requireNonNull(amount, "amount");
    }

    public Status status() {
        return state.status();
    }

    /** Zwraca kwotę pobraną do kasy. */
    public Money pay() {
        return state.pay(this);
    }

    public void use() {
        state.use(this);
    }

    /** Zwraca kwotę oddaną klientowi (zero, gdy rezerwacja nie była opłacona). */
    public Money cancel() {
        return state.cancel(this);
    }

    /** Dla cofania poleceń: powrót do zapamiętanego stanu z pominięciem reguł przejść. */
    void restore(Status status) {
        state = State.valueOf(status.name());
    }

    private enum State {
        NEW {
            @Override
            Money pay(Booking booking) {
                booking.state = PAID;
                return booking.amount;
            }

            @Override
            Money cancel(Booking booking) {
                booking.state = CANCELLED;
                return Money.ZERO;
            }
        },
        PAID {
            @Override
            void use(Booking booking) {
                booking.state = USED;
            }

            @Override
            Money cancel(Booking booking) {
                booking.state = CANCELLED;
                return booking.amount;
            }
        },
        USED,
        CANCELLED;

        Money pay(Booking booking) {
            throw invalid("pay");
        }

        void use(Booking booking) {
            throw invalid("use");
        }

        Money cancel(Booking booking) {
            throw invalid("cancel");
        }

        Status status() {
            return Status.valueOf(name());
        }

        private IllegalStateException invalid(String action) {
            return new IllegalStateException("cannot " + action + " in " + status());
        }
    }
}
