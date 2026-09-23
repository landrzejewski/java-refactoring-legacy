package pl.training.workshop.m5.s02_pullupfield.step1;

import java.util.Locale;

/** Krok 1: bez zmian - normalizacja VIP zostaje. */
public final class VipTicket extends Ticket {
    private final String seat;

    public VipTicket(String seat) {
        this.seat = seat.toUpperCase(Locale.ROOT);
    }

    public String seat() {
        return seat;
    }

    @Override
    public String describe() {
        return "VIP " + seat;
    }
}
