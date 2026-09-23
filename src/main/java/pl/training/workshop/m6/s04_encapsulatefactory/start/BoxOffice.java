package pl.training.workshop.m6.s04_encapsulatefactory.start;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.m6.s04_encapsulatefactory.start.tickets.StandardTicket;
import pl.training.workshop.m6.s04_encapsulatefactory.start.tickets.Ticket;
import pl.training.workshop.m6.s04_encapsulatefactory.start.tickets.VipTicket;
import pl.training.workshop.shared.Money;

/**
 * Start: klient zna klasy konkretne biletów i regułę VIP (rząd 10+), tworzy je przez new
 * w dwóch miejscach. Nowy rodzaj biletu = zmiany u każdego klienta.
 */
public final class BoxOffice {
    public Ticket sell(String title, Money base, int row) {
        if (row >= 10) {
            return new VipTicket(title, base, row);
        }
        return new StandardTicket(title, base, row);
    }

    public List<Ticket> sellAll(String title, Money base, List<Integer> rows) {
        List<Ticket> tickets = new ArrayList<>();
        for (int row : rows) {
            tickets.add(row >= 10
                    ? new VipTicket(title, base, row)
                    : new StandardTicket(title, base, row));
        }
        return tickets;
    }
}
