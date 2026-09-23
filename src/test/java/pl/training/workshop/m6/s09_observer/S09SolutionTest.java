package pl.training.workshop.m6.s09_observer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m6.s09_observer.step3.PaymentListener;
import pl.training.workshop.m6.s09_observer.step3.PaymentService;
import pl.training.workshop.m6.s09_observer.step3.Subscription;
import pl.training.workshop.shared.Money;

/** Kontrakt subjectu po refaktoryzacji: rejestracje, wyrejestrowanie, rozszerzenie. */
final class S09SolutionTest {
    private final Payment payment = new Payment("R7", "jan@kino.pl", "600", Money.of("25.00"));

    @Test
    void sameListenerSubscribedTwiceIsNotifiedTwice() {
        List<String> log = new ArrayList<>();
        PaymentListener listener = event -> log.add(event.reservationId());
        PaymentService service = new PaymentService();
        service.subscribe(listener);
        service.subscribe(listener);
        service.confirm(payment);
        assertEquals(List.of("R7", "R7"), log);
    }

    @Test
    void closingSubscriptionIsIdempotentAndRemovesOnlyItsOwnRegistration() {
        List<String> log = new ArrayList<>();
        PaymentListener listener = event -> log.add(event.reservationId());
        PaymentService service = new PaymentService();
        Subscription first = service.subscribe(listener);
        service.subscribe(listener);
        first.close();
        first.close();
        service.confirm(payment);
        assertEquals(List.of("R7"), log);
    }

    @Test
    void newReceiverIsAnExtensionNotARefactoring() {
        List<String> log = new ArrayList<>();
        PaymentService service = new PaymentService();
        service.subscribe(event -> log.add("push " + event.reservationId()));
        service.confirm(payment);
        assertEquals(List.of("push R7"), log);
    }
}
