package pl.training.workshop.exercises.e04_seatpicker.step2

/** Krok 2: GLASSES - własne albo wypożyczone okulary; cofnięcie przywraca poprzedni wybór. */
class SetGlasses(private val cart: Cart, private val own: Boolean) : Command {
    private var before = false

    override fun execute() {
        before = cart.ownGlasses
        cart.ownGlasses = own
    }

    override fun undo() {
        cart.ownGlasses = before
    }
}
