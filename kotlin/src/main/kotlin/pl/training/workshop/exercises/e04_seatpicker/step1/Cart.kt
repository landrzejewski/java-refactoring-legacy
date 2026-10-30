package pl.training.workshop.exercises.e04_seatpicker.step1

import pl.training.workshop.exercises.e04_seatpicker.HallPlan
import pl.training.workshop.exercises.e04_seatpicker.Seat
import pl.training.workshop.shared.Money

/**
 * Krok 1: odbiorca poleceń (Extract Class z SeatPicker) - stan koszyka i elementarne operacje
 * z walidacją. Nie zna nazw operacji kasjera ani historii.
 */
class Cart(val plan: HallPlan) {
    private val seats = mutableListOf<Seat>()
    var ownGlasses = false
    var voucher: String? = null

    fun seats(): List<Seat> = seats.toList()

    fun add(seat: Seat) {
        checkFree(seat)
        if (seats.size >= LIMIT) {
            throw IllegalStateException("limit $LIMIT miejsc")
        }
        seats += seat
    }

    /** Usuwa miejsce i zwraca jego pozycję, żeby cofnięcie mogło ją przywrócić. */
    fun remove(seat: Seat): Int {
        val index = seats.indexOf(seat)
        if (index < 0) {
            throw IllegalArgumentException("miejsca $seat nie ma w koszyku")
        }
        seats.removeAt(index)
        return index
    }

    fun insert(index: Int, seat: Seat) {
        seats.add(index, seat)
    }

    /** Zamienia miejsce na inne na tej samej pozycji i zwraca tę pozycję. */
    fun replace(from: Seat, to: Seat): Int {
        val index = seats.indexOf(from)
        if (index < 0) {
            throw IllegalArgumentException("miejsca $from nie ma w koszyku")
        }
        checkFree(to)
        seats[index] = to
        return index
    }

    fun replaceAt(index: Int, seat: Seat) {
        seats[index] = seat
    }

    fun clear() {
        seats.clear()
    }

    fun replaceAll(newSeats: List<Seat>) {
        seats.clear()
        seats.addAll(newSeats)
    }

    private fun checkFree(seat: Seat) {
        if (!plan.exists(seat)) {
            throw IllegalArgumentException("nie ma miejsca $seat")
        }
        if (plan.isTaken(seat)) {
            throw IllegalStateException("miejsce $seat jest zajete")
        }
        if (seat in seats) {
            throw IllegalStateException("miejsce $seat jest juz w koszyku")
        }
    }

    fun state(): String {
        val list = if (seats.isEmpty()) "-" else seats.joinToString(" ")
        return "miejsca: $list" +
            " | okulary: ${if (ownGlasses) "wlasne" else "wypozyczone"}" +
            " | voucher: ${voucher ?: "-"}" +
            " | suma: ${total()}"
    }

    private fun total(): Money {
        var sum = Money.ZERO
        for (seat in seats) {
            var price = BASE
            if (plan.isVip(seat)) {
                price += VIP
            }
            if (!ownGlasses) {
                price += GLASSES
            }
            sum += price
        }
        if (voucher == "KINO10") {
            sum -= sum.percent(10)
        }
        return sum
    }

    private companion object {
        val BASE = Money.of("32.00")
        val VIP = Money.of("10.00")
        val GLASSES = Money.of("3.00")
        const val LIMIT = 4
    }
}
