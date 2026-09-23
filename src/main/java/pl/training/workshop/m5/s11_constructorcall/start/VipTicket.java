package pl.training.workshop.m5.s11_constructorcall.start;

/** Start: pole lounge jest jeszcze null, gdy baza woła describe() - etykieta zawiera "null" na zawsze. */
public class VipTicket extends Ticket {
    private final String lounge;

    public VipTicket(String seat, String lounge) {
        super(seat);
        this.lounge = lounge;
    }

    @Override
    protected String describe() {
        return super.describe() + " (VIP: " + lounge + ")";
    }
}
