package pl.training.workshop.m5.s02_pullupfield.step2;

/**
 * Krok 2: ujednolicenie cyklu życia - miejsce przez konstruktor, pole final, setter usunięty.
 * Dopiero teraz wszystkie trzy pola "seat" mają ten sam typ, znaczenie i moment inicjalizacji.
 */
public final class StandardTicket extends Ticket {
    private final String seat;

    public StandardTicket(String seat) {
        this.seat = seat;
    }

    public String seat() {
        return seat;
    }

    @Override
    public String describe() {
        return "NORMAL " + seat;
    }
}
