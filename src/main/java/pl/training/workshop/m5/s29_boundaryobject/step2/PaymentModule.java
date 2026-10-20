package pl.training.workshop.m5.s29_boundaryobject.step2;

import pl.training.workshop.m5.s29_boundaryobject.LegacyTerminalClient;

/** Stabilny punkt wejścia sceny (korzeń kompozycji): skąd serwis bierze terminal. Krok 2: bez zmian. */
public final class PaymentModule {
    private PaymentModule() {
    }

    public static PaymentService paymentService(LegacyTerminalClient terminal) {
        return new PaymentService(terminal);
    }
}
