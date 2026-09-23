package pl.training.workshop.m6.s08_state.step2;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.m6.s08_state.Payments;
import pl.training.workshop.m6.s08_state.ReservationActions;
import pl.training.workshop.m6.s08_state.Status;

/**
 * Krok 2: pay i use przeniesione do stanów. Domyślna metoda rzuca wyjątek (nie jest pusta!),
 * a stan nadpisuje tylko dozwolone przejścia. Kolejność: obciążenie, zmiana stanu, efekt.
 */
public final class Reservation implements ReservationActions {
    private final String id;
    private final Payments payments;
    private final List<String> effects = new ArrayList<>();
    private ReservationState state = NewState.INSTANCE;

    public Reservation(String id, Payments payments) {
        this.id = id;
        this.payments = payments;
    }

    @Override
    public void pay() {
        state.pay(this);
    }

    @Override
    public void use() {
        state.use(this);
    }

    @Override
    public void expire() {
        if (state.status() != Status.NEW) {
            throw new IllegalStateException("cannot expire in " + state.status());
        }
        moveTo(ClosedState.EXPIRED);
        record("seats released");
    }

    @Override
    public void cancel() {
        if (state.status() == Status.NEW) {
            moveTo(ClosedState.CANCELLED);
            record("seats released");
        } else if (state.status() == Status.PAID) {
            moveTo(ClosedState.CANCELLED);
            record("refund");
            record("seats released");
        } else {
            throw new IllegalStateException("cannot cancel in " + state.status());
        }
    }

    @Override
    public Status status() {
        return state.status();
    }

    @Override
    public List<String> effects() {
        return List.copyOf(effects);
    }

    private void charge() {
        payments.charge(id);
    }

    private void moveTo(ReservationState next) {
        state = next;
    }

    private void record(String effect) {
        effects.add(effect);
    }

    private sealed interface ReservationState permits NewState, PaidState, ClosedState {
        Status status();

        default void pay(Reservation reservation) {
            throw invalid("pay");
        }

        default void use(Reservation reservation) {
            throw invalid("use");
        }

        private IllegalStateException invalid(String action) {
            return new IllegalStateException("cannot " + action + " in " + status());
        }
    }

    private enum NewState implements ReservationState {
        INSTANCE;

        @Override
        public Status status() {
            return Status.NEW;
        }

        @Override
        public void pay(Reservation reservation) {
            reservation.charge();
            reservation.moveTo(PaidState.INSTANCE);
            reservation.record("charged");
        }
    }

    private enum PaidState implements ReservationState {
        INSTANCE;

        @Override
        public Status status() {
            return Status.PAID;
        }

        @Override
        public void use(Reservation reservation) {
            reservation.moveTo(ClosedState.USED);
            reservation.record("gate opened");
        }
    }

    /** Stany końcowe - żadne przejście nie jest dozwolone. */
    private enum ClosedState implements ReservationState {
        USED(Status.USED), EXPIRED(Status.EXPIRED), CANCELLED(Status.CANCELLED);

        private final Status status;

        ClosedState(Status status) {
            this.status = status;
        }

        @Override
        public Status status() {
            return status;
        }
    }
}
