package pl.training.workshop.m3.s16_temporalcoupling.step2;

import pl.training.workshop.m3.s16_temporalcoupling.Screening;

/** Krok 2 (rozwiązanie): drukarka dostaje kompletny {@link TicketRequest}. */
public final class TicketPrinter {
    public String print(TicketRequest request) {
        Screening screening = request.screening();
        return "BILET " + screening.title() + " (" + screening.format() + ") " + screening.start()
                + ", miejsce " + request.seat() + ", dla " + request.buyer().toLowerCase();
    }
}
