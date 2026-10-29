package pl.training.workshop.exercises.e04_seatpicker.step2;

import pl.training.workshop.exercises.e04_seatpicker.Seat;

/** Krok 2: DESELECT - usunięcie miejsca; cofnięcie wstawia je na tę samą pozycję. */
public final class DeselectSeat implements Command {
    private final Cart cart;
    private final Seat seat;
    private int index;

    public DeselectSeat(Cart cart, Seat seat) {
        this.cart = cart;
        this.seat = seat;
    }

    @Override
    public void execute() {
        index = cart.remove(seat);
    }

    @Override
    public void undo() {
        cart.insert(index, seat);
    }
}
