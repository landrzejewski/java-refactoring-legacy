package pl.training.workshop.m5.s02_pullupfield.step1;

/** Krok 1: bez zmian - miejsce nadal ustawiane setterem. */
public final class StandardTicket extends Ticket {
    private String seat;

    public void setSeat(String seat) {
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
