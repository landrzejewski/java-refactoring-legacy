package pl.training.workshop.m6.s04_encapsulatefactory.step3.tickets;

import pl.training.workshop.shared.Money;

/** Krok 3: fabryka jest jedynym publicznym wejściem do tworzenia biletów. */
public final class Tickets {
    private static final int VIP_FROM_ROW = 10;

    private Tickets() {
    }

    public static Ticket forSeat(String title, Money base, int row) {
        if (row >= VIP_FROM_ROW) {
            return new VipTicket(title, base, row);
        }
        return new StandardTicket(title, base, row);
    }
}
