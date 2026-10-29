package pl.training.workshop.exercises.e04_seatpicker.step1;

import pl.training.workshop.exercises.e04_seatpicker.Seat;

/** Krok 1: SWAP - zamiana miejsca na inne bez zmiany kolejności; cofnięcie przywraca stare. */
public final class SwapSeats implements Command {
    private final Cart cart;
    private final Seat from;
    private final Seat to;
    private int index;

    public SwapSeats(Cart cart, Seat from, Seat to) {
        this.cart = cart;
        this.from = from;
        this.to = to;
    }

    @Override
    public void execute() {
        index = cart.replace(from, to);
    }

    @Override
    public void undo() {
        cart.replaceAt(index, from);
    }
}
