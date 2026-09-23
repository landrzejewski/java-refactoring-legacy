package pl.training.workshop.m5.s11_constructorcall.step2;

/** Krok 2: zwykła kolejność super(...), potem pola - poprawność nie zależy już od kolejności inicjalizacji. */
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
