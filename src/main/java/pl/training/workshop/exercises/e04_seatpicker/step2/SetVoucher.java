package pl.training.workshop.exercises.e04_seatpicker.step2;

/** Krok 2: VOUCHER - ustawienie albo zdjęcie kodu; cofnięcie przywraca poprzedni kod. */
public final class SetVoucher implements Command {
    private final Cart cart;
    private final String code;
    private String before;

    public SetVoucher(Cart cart, String code) {
        this.cart = cart;
        this.code = code;
    }

    @Override
    public void execute() {
        before = cart.voucher();
        cart.setVoucher(code);
    }

    @Override
    public void undo() {
        cart.setVoucher(before);
    }
}
