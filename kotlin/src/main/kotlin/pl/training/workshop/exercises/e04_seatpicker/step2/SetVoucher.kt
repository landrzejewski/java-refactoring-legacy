package pl.training.workshop.exercises.e04_seatpicker.step2

/** Krok 2: VOUCHER - ustawienie albo zdjęcie kodu; cofnięcie przywraca poprzedni kod. */
class SetVoucher(private val cart: Cart, private val code: String?) : Command {
    private var before: String? = null

    override fun execute() {
        before = cart.voucher
        cart.voucher = code
    }

    override fun undo() {
        cart.voucher = before
    }
}
