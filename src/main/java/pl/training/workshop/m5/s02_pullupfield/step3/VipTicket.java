package pl.training.workshop.m5.s02_pullupfield.step3;

import java.util.Locale;

/** Krok 3: reguła normalizacji VIP zostaje w podklasie - przekazuje do bazy już znormalizowaną wartość. */
public final class VipTicket extends Ticket {
    public VipTicket(String seat) {
        super(seat.toUpperCase(Locale.ROOT));
    }

    @Override
    public String describe() {
        return "VIP " + seat();
    }
}
