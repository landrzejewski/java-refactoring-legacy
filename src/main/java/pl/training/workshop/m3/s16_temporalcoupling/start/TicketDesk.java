package pl.training.workshop.m3.s16_temporalcoupling.start;

import pl.training.workshop.m3.s16_temporalcoupling.Screening;

/**
 * Klient drukarki: kasa wydająca bilety. Musi znać protokół drukarki
 * (trzy wywołania przed print). Test woła tylko {@link #issue}.
 */
public final class TicketDesk {
    private final TicketPrinter printer = new TicketPrinter();

    public String issue(Screening screening, int seat, String buyer) {
        printer.selectScreening(screening);
        printer.selectSeat(seat);
        printer.forBuyer(buyer);
        return printer.print();
    }
}
