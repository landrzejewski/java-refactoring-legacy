package pl.training.workshop.m5.s01_pullupmethod.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: klient tworzy konkretny bilet w jednym miejscu, a dalej pracuje na typie bazowym. */
public final class BoxOffice {
    public String label(String kind, String title, Money basePrice) {
        Ticket ticket = switch (kind) {
            case "STUDENT" -> new StudentTicket(title, basePrice);
            case "VIP" -> new VipTicket(title, basePrice);
            default -> new StandardTicket(title, basePrice);
        };
        return ticket.label();
    }
}
