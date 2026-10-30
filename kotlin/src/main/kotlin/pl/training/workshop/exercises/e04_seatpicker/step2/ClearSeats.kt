package pl.training.workshop.exercises.e04_seatpicker.step2

import pl.training.workshop.exercises.e04_seatpicker.Seat

/** Krok 2: CLEAR - opróżnienie listy miejsc; cofnięcie dopisuje zapamiętane miejsca. */
class ClearSeats(private val cart: Cart) : Command {
    private var before = listOf<Seat>()

    override fun execute() {
        before = cart.seats()
        cart.clear()
    }

    override fun undo() {
        for (seat in before) {
            cart.insert(cart.seats().size, seat)
        }
    }
}
