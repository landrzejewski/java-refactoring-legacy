package pl.training.workshop.m3.s16_temporalcoupling.step1;

import pl.training.workshop.m3.s16_temporalcoupling.Screening;

/** Krok 1: kasa po Change Signature - jedno wywołanie, bez protokołu. */
public final class TicketDesk {
    private final TicketPrinter printer = new TicketPrinter();

    public String issue(Screening screening, int seat, String buyer) {
        return printer.print(screening, seat, buyer);
    }
}
