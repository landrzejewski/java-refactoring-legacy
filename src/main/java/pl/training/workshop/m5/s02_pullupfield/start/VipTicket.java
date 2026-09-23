package pl.training.workshop.m5.s02_pullupfield.start;

import java.util.Locale;

/** Start: miejsce normalizowane do wielkich liter - ta reguła dotyczy tylko VIP. */
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
