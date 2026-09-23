package pl.training.workshop.m6.s08_state.step3;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.m6.s08_state.Payments;
import pl.training.workshop.m6.s08_state.ReservationActions;
import pl.training.workshop.m6.s08_state.Status;

/**
 * Krok 3: expire i cancel w stanach - kontekst tylko deleguje. Tabela przejść jest czytelna
 * wprost z kodu stanów; stany są bezstanowymi stałymi enum, dane zostają w Reservation.
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
        state.expire(this);
    }

    @Override
    public void cancel() {
        state.cancel(this);
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

        default void expire(Reservation reservation) {
            throw invalid("expire");
        }

        default void cancel(Reservation reservation) {
            throw invalid("cancel");
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

        @Override
        public void expire(Reservation reservation) {
            reservation.moveTo(ClosedState.EXPIRED);
            reservation.record("seats released");
        }

        @Override
        public void cancel(Reservation reservation) {
            reservation.moveTo(ClosedState.CANCELLED);
            reservation.record("seats released");
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

        @Override
        public void cancel(Reservation reservation) {
            reservation.moveTo(ClosedState.CANCELLED);
            reservation.record("refund");
            reservation.record("seats released");
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
