package pl.training.workshop.m5.s29_boundaryobject.step3;

import pl.training.workshop.m5.s29_boundaryobject.LegacyTerminalClient;

/** Stabilny punkt wejścia sceny (korzeń kompozycji). Krok 3: tu, i tylko tu, terminal dostaje adapter. */
public final class PaymentModule {
    private PaymentModule() {
    }

    public static PaymentService paymentService(LegacyTerminalClient terminal) {
        return new PaymentService(new LegacyTerminalAdapter(terminal));
    }
}
