package pl.training.workshop.m6.s04_encapsulatefactory.step1;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.m6.s04_encapsulatefactory.step1.tickets.Ticket;
import pl.training.workshop.m6.s04_encapsulatefactory.step1.tickets.Tickets;
import pl.training.workshop.shared.Money;

/**
 * Krok 1: każde new zastąpione wywołaniem metody tworzącej. Klient nie importuje już
 * klas konkretnych, ale nadal zna regułę VIP.
 */
public final class BoxOffice {
    public Ticket sell(String title, Money base, int row) {
        if (row >= 10) {
            return Tickets.vip(title, base, row);
        }
        return Tickets.standard(title, base, row);
    }

    public List<Ticket> sellAll(String title, Money base, List<Integer> rows) {
        List<Ticket> tickets = new ArrayList<>();
        for (int row : rows) {
            tickets.add(row >= 10 ? Tickets.vip(title, base, row) : Tickets.standard(title, base, row));
        }
        return tickets;
    }
}
