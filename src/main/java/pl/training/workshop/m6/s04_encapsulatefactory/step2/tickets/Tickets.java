package pl.training.workshop.m6.s04_encapsulatefactory.step2.tickets;

import pl.training.workshop.shared.Money;

/** Krok 2: Move Method - decyzja "który bilet" należy do fabryki, nie do klienta. */
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
