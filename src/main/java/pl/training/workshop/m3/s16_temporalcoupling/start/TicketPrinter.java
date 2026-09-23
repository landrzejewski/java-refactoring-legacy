package pl.training.workshop.m3.s16_temporalcoupling.start;

import pl.training.workshop.m3.s16_temporalcoupling.Screening;

/**
 * Start: sprzężenie czasowe (protokół wywołań). Żeby wydrukować bilet, trzeba najpierw
 * wywołać selectScreening, selectSeat i forBuyer - w dowolnej kolejności, ale wszystkie.
 * Nic w typach tego nie mówi: zapomniane wywołanie to NullPointerException w print,
 * a obiekt współdzielony przez dwie kasy miesza dane klientów.
 */
public final class TicketPrinter {
    private Screening screening;
    private Integer seat;
    private String buyer;

    public void selectScreening(Screening screening) {
        this.screening = screening;
    }

    public void selectSeat(int seat) {
        this.seat = seat;
    }

    public void forBuyer(String email) {
        this.buyer = email;
    }

    public String print() {
        return "BILET " + screening.title() + " (" + screening.format() + ") " + screening.start()
                + ", miejsce " + seat.intValue() + ", dla " + buyer.toLowerCase();
    }
}
