package pl.training.workshop.m5.s01_pullupmethod.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: bez zmian - klient nadal zna konkretne klasy (label() jeszcze nie ma w bazie). */
public final class BoxOffice {
    public String label(String kind, String title, Money basePrice) {
        return switch (kind) {
            case "STUDENT" -> new StudentTicket(title, basePrice).label();
            case "VIP" -> new VipTicket(title, basePrice).label();
            default -> new StandardTicket(title, basePrice).label();
        };
    }
}
