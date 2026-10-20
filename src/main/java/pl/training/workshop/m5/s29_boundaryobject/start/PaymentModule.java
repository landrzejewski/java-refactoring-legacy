package pl.training.workshop.m5.s29_boundaryobject.start;

import pl.training.workshop.m5.s29_boundaryobject.LegacyTerminalClient;

/** Stabilny punkt wejścia sceny (korzeń kompozycji): skąd serwis bierze terminal. Start: wprost. */
public final class PaymentModule {
    private PaymentModule() {
    }

    public static PaymentService paymentService(LegacyTerminalClient terminal) {
        return new PaymentService(terminal);
    }
}
