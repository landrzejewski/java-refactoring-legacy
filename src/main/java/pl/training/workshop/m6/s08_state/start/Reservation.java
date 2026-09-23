package pl.training.workshop.m6.s08_state.start;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.m6.s08_state.Payments;
import pl.training.workshop.m6.s08_state.ReservationActions;
import pl.training.workshop.m6.s08_state.Status;

/**
 * Start: każda operacja sprawdza status warunkami i sama go zmienia. Reguły przejść
 * NEW -&gt; PAID -&gt; USED, NEW -&gt; EXPIRED, NEW/PAID -&gt; CANCELLED są rozsiane po metodach.
 */
public final class Reservation implements ReservationActions {
    private final String id;
    private final Payments payments;
    private final List<String> effects = new ArrayList<>();
    private Status status = Status.NEW;

    public Reservation(String id, Payments payments) {
        this.id = id;
        this.payments = payments;
    }

    @Override
    public void pay() {
        if (status != Status.NEW) {
            throw new IllegalStateException("cannot pay in " + status);
        }
        payments.charge(id);
        status = Status.PAID;
        effects.add("charged");
    }

    @Override
    public void use() {
        if (status != Status.PAID) {
            throw new IllegalStateException("cannot use in " + status);
        }
        status = Status.USED;
        effects.add("gate opened");
    }

    @Override
    public void expire() {
        if (status != Status.NEW) {
            throw new IllegalStateException("cannot expire in " + status);
        }
        status = Status.EXPIRED;
        effects.add("seats released");
    }

    @Override
    public void cancel() {
        if (status == Status.NEW) {
            status = Status.CANCELLED;
            effects.add("seats released");
        } else if (status == Status.PAID) {
            status = Status.CANCELLED;
            effects.add("refund");
            effects.add("seats released");
        } else {
            throw new IllegalStateException("cannot cancel in " + status);
        }
    }

    @Override
    public Status status() {
        return status;
    }

    @Override
    public List<String> effects() {
        return List.copyOf(effects);
    }
}
