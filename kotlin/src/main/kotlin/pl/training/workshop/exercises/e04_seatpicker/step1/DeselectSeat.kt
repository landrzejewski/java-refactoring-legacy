package pl.training.workshop.exercises.e04_seatpicker.step1

import pl.training.workshop.exercises.e04_seatpicker.Seat

/** Krok 1: DESELECT - usunięcie miejsca; cofnięcie wstawia je na tę samą pozycję. */
class DeselectSeat(private val cart: Cart, private val seat: Seat) : Command {
    private var index = 0

    override fun execute() {
        index = cart.remove(seat)
    }

    override fun undo() {
        cart.insert(index, seat)
    }
}
