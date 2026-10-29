package pl.training.workshop.exercises.e04_seatpicker.step1;

import pl.training.workshop.exercises.e04_seatpicker.Seat;

/** Krok 1: SELECT - dodanie miejsca; cofnięcie je usuwa. */
public final class SelectSeat implements Command {
    private final Cart cart;
    private final Seat seat;

    public SelectSeat(Cart cart, Seat seat) {
        this.cart = cart;
        this.seat = seat;
    }

    @Override
    public void execute() {
        cart.add(seat);
    }

    @Override
    public void undo() {
        cart.remove(seat);
    }
}
