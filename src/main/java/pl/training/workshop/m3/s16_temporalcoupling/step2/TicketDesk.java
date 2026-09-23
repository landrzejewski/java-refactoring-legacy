package pl.training.workshop.m3.s16_temporalcoupling.step2;

import pl.training.workshop.m3.s16_temporalcoupling.Screening;

/** Krok 2: kasa buduje kompletne żądanie biletu. */
public final class TicketDesk {
    private final TicketPrinter printer = new TicketPrinter();

    public String issue(Screening screening, int seat, String buyer) {
        return printer.print(new TicketRequest(screening, seat, buyer));
    }
}
