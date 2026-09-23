package pl.training.workshop.m5.s01_pullupmethod.start;

import pl.training.workshop.shared.Money;

/** Start: klient musi znać konkretną klasę, bo {@code label()} nie istnieje w typie bazowym. */
public final class BoxOffice {
    public String label(String kind, String title, Money basePrice) {
        return switch (kind) {
            case "STUDENT" -> new StudentTicket(title, basePrice).label();
            case "VIP" -> new VipTicket(title, basePrice).label();
            default -> new StandardTicket(title, basePrice).label();
        };
    }
}
