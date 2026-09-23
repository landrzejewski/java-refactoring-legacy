package pl.training.workshop.m5.s02_pullupfield.start;

/** Start: miejsce ustawiane setterem po utworzeniu - pole mutowalne. */
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
