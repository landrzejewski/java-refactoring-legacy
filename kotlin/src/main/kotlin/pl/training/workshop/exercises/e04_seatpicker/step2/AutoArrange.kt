package pl.training.workshop.exercises.e04_seatpicker.step2

import pl.training.workshop.exercises.e04_seatpicker.HallPlan
import pl.training.workshop.exercises.e04_seatpicker.Seat

/**
 * Krok 2: AUTO - przed zmianą prosi koszyk o migawkę, a przywraca ją zarówno przy awarii, jak i przy
 * cofnięciu. Polecenie nie zna już pól koszyka. Świadoma zmiana zachowania: migawka obejmuje cały
 * koszyk, więc voucher nie ginie (w start i kroku 1 ginął, bo ręczna kopia go nie obejmowała).
 */
class AutoArrange(private val cart: Cart, private val count: Int) : Command {
    private lateinit var before: Cart.Memento

    override fun execute() {
        if (count < 1 || count > LIMIT) {
            throw IllegalArgumentException("limit $LIMIT miejsc")
        }
        before = cart.snapshot()
        cart.clear()
        cart.ownGlasses = false
        cart.voucher = null
        try {
            val plan = cart.plan
            for (row in plan.rowsFromMiddle()) {
                val first = freeRun(plan, row)
                if (first > 0) {
                    for (i in 0 until count) {
                        cart.add(Seat(row, first + i))
                    }
                    return
                }
            }
            throw IllegalStateException("brak $count miejsc obok siebie")
        } catch (exception: IllegalStateException) {
            undo()
            throw exception
        }
    }

    override fun undo() {
        cart.restore(before)
    }

    /** Numer pierwszego miejsca ciągu `count` wolnych miejsc w rzędzie albo 0. */
    private fun freeRun(plan: HallPlan, row: Int): Int {
        var run = 0
        for (number in 1..plan.seatsPerRow) {
            run = if (plan.isTaken(Seat(row, number))) 0 else run + 1
            if (run == count) {
                return number - count + 1
            }
        }
        return 0
    }

    private companion object {
        const val LIMIT = 4
    }
}
