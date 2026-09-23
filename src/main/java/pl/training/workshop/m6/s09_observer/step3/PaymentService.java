package pl.training.workshop.m6.s09_observer.step3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import pl.training.workshop.m6.s09_observer.Payment;

/**
 * Krok 3: subject z subscribe - serwis nie zna już żadnego konkretnego odbiorcy.
 * Kontrakt: synchronicznie, w kolejności subskrypcji, fail-fast, iteracja po migawce listy.
 */
public final class PaymentService {
    private final List<Registration> listeners = new CopyOnWriteArrayList<>();
    private final List<String> paid = new ArrayList<>();

    public Subscription subscribe(PaymentListener listener) {
        Registration registration = new Registration(Objects.requireNonNull(listener, "listener"));
        listeners.add(registration);
        AtomicBoolean active = new AtomicBoolean(true);
        return () -> {
            if (active.compareAndSet(true, false)) {
                listeners.remove(registration);
            }
        };
    }

    public void confirm(Payment payment) {
        if (payment.amount().amount().signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        paid.add(payment.reservationId());
        ReservationPaid event = new ReservationPaid(
                payment.reservationId(), payment.email(), payment.phone(), payment.amount());
        for (Registration registration : listeners) {
            registration.listener().onPaid(event);
        }
    }

    public List<String> paid() {
        return List.copyOf(paid);
    }

    /** Osobny obiekt na każde subscribe - ta sama lambda zapisana dwa razy to dwie rejestracje. */
    private static final class Registration {
        private final PaymentListener listener;

        private Registration(PaymentListener listener) {
            this.listener = listener;
        }

        PaymentListener listener() {
            return listener;
        }
    }
}
