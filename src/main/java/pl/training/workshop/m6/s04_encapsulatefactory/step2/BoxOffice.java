package pl.training.workshop.m6.s04_encapsulatefactory.step2;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.m6.s04_encapsulatefactory.step2.tickets.Ticket;
import pl.training.workshop.m6.s04_encapsulatefactory.step2.tickets.Tickets;
import pl.training.workshop.shared.Money;

/** Krok 2: klient prosi o bilet dla miejsca - reguła VIP jest w jednym miejscu. */
public final class BoxOffice {
    public Ticket sell(String title, Money base, int row) {
        return Tickets.forSeat(title, base, row);
    }

    public List<Ticket> sellAll(String title, Money base, List<Integer> rows) {
        List<Ticket> tickets = new ArrayList<>();
        for (int row : rows) {
            tickets.add(sell(title, base, row));
        }
        return tickets;
    }
}
