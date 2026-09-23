package pl.training.workshop.m6.s08_state.step1;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.m6.s08_state.Payments;
import pl.training.workshop.m6.s08_state.ReservationActions;
import pl.training.workshop.m6.s08_state.Status;

/**
 * Krok 1: pole status zastąpione obiektem stanu (na razie zna tylko swój Status).
 * Warunki bez zmian - porównują state.status(). Efekty i zmiana stanu w metodach pomocniczych.
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
        if (state.status() != Status.NEW) {
            throw new IllegalStateException("cannot pay in " + state.status());
        }
        charge();
        moveTo(PaidState.INSTANCE);
        record("charged");
    }

    @Override
    public void use() {
        if (state.status() != Status.PAID) {
            throw new IllegalStateException("cannot use in " + state.status());
        }
        moveTo(ClosedState.USED);
        record("gate opened");
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
    }

    private enum NewState implements ReservationState {
        INSTANCE;

        @Override
        public Status status() {
            return Status.NEW;
        }
    }

    private enum PaidState implements ReservationState {
        INSTANCE;

        @Override
        public Status status() {
            return Status.PAID;
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
