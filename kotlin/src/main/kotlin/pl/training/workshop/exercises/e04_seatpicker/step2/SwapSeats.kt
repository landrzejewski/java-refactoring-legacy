package pl.training.workshop.exercises.e04_seatpicker.step2

import pl.training.workshop.exercises.e04_seatpicker.Seat

/** Krok 2: SWAP - zamiana miejsca na inne bez zmiany kolejności; cofnięcie przywraca stare. */
class SwapSeats(private val cart: Cart, private val from: Seat, private val to: Seat) : Command {
    private var index = 0

    override fun execute() {
        index = cart.replace(from, to)
    }

    override fun undo() {
        cart.replaceAt(index, from)
    }
}
