package pl.training.workshop.m6.s04_encapsulatefactory.step1.tickets;

import pl.training.workshop.shared.Money;

/** Krok 1: metody tworzące (Creation Method) obok klas biletów - zwracają typ Ticket. */
public final class Tickets {
    private Tickets() {
    }

    public static Ticket standard(String title, Money base, int row) {
        return new StandardTicket(title, base, row);
    }

    public static Ticket vip(String title, Money base, int row) {
        return new VipTicket(title, base, row);
    }
}
