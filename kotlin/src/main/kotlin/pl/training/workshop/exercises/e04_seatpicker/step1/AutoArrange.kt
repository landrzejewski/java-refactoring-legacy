package pl.training.workshop.exercises.e04_seatpicker.step1

import pl.training.workshop.exercises.e04_seatpicker.HallPlan
import pl.training.workshop.exercises.e04_seatpicker.Seat

/**
 * Krok 1: AUTO - czyści koszyk i wybiera `count` wolnych miejsc obok siebie w najlepszym
 * rzędzie. Punkt kontrolny przeniesiony z SeatPicker bez zmian: polecenie samo kopiuje pola
 * koszyka (miejsca i okulary) i samo je przywraca, więc musi znać budowę koszyka. Voucher nadal
 * nie jest kopiowany - to ten sam zapach, tylko w innym miejscu.
 */
class AutoArrange(private val cart: Cart, private val count: Int) : Command {
    private var seatsBefore = listOf<Seat>()
    private var glassesBefore = false

    override fun execute() {
        if (count < 1 || count > LIMIT) {
            throw IllegalArgumentException("limit $LIMIT miejsc")
        }
        seatsBefore = cart.seats()
        glassesBefore = cart.ownGlasses
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
        cart.replaceAll(seatsBefore)
        cart.ownGlasses = glassesBefore
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
