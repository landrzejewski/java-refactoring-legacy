package pl.training.workshop.exercises.e04_seatpicker.step1

import pl.training.workshop.exercises.e04_seatpicker.Seat

/** Krok 1: SELECT - dodanie miejsca; cofnięcie je usuwa. */
class SelectSeat(private val cart: Cart, private val seat: Seat) : Command {
    override fun execute() {
        cart.add(seat)
    }

    override fun undo() {
        cart.remove(seat)
    }
}
