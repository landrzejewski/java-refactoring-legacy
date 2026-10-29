package pl.training.workshop.exercises.e04_seatpicker.step1;

import java.util.List;

import pl.training.workshop.exercises.e04_seatpicker.Seat;

/** Krok 1: CLEAR - opróżnienie listy miejsc; cofnięcie dopisuje zapamiętane miejsca. */
public final class ClearSeats implements Command {
    private final Cart cart;
    private List<Seat> before = List.of();

    public ClearSeats(Cart cart) {
        this.cart = cart;
    }

    @Override
    public void execute() {
        before = cart.seats();
        cart.clear();
    }

    @Override
    public void undo() {
        for (Seat seat : before) {
            cart.insert(cart.seats().size(), seat);
        }
    }
}
