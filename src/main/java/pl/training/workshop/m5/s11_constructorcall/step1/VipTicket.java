package pl.training.workshop.m5.s11_constructorcall.step1;

/**
 * Krok 1: szybka naprawa w Javie 25 - prolog konstruktora (JEP 513). Pole przypisane PRZED super(...),
 * więc override widzi wartość. Działa, ale kruche: każda nowa podklasa musi o tym pamiętać.
 */
public class VipTicket extends Ticket {
    private final String lounge;

    public VipTicket(String seat, String lounge) {
        this.lounge = lounge;
        super(seat);
    }

    @Override
    protected String describe() {
        return super.describe() + " (VIP: " + lounge + ")";
    }
}
