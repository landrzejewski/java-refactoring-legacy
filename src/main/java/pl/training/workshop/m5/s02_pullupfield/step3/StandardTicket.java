package pl.training.workshop.m5.s02_pullupfield.step3;

/** Krok 3: stan miejsca przeniesiony do bazy, podklasa tylko go opisuje. */
public final class StandardTicket extends Ticket {
    public StandardTicket(String seat) {
        super(seat);
    }

    @Override
    public String describe() {
        return "NORMAL " + seat();
    }
}
