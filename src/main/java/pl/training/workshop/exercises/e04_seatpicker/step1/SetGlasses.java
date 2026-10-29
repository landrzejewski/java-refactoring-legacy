package pl.training.workshop.exercises.e04_seatpicker.step1;

/** Krok 1: GLASSES - własne albo wypożyczone okulary; cofnięcie przywraca poprzedni wybór. */
public final class SetGlasses implements Command {
    private final Cart cart;
    private final boolean own;
    private boolean before;

    public SetGlasses(Cart cart, boolean own) {
        this.cart = cart;
        this.own = own;
    }

    @Override
    public void execute() {
        before = cart.ownGlasses();
        cart.setOwnGlasses(own);
    }

    @Override
    public void undo() {
        cart.setOwnGlasses(before);
    }
}
