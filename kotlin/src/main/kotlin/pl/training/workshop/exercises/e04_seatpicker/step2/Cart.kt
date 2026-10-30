package pl.training.workshop.exercises.e04_seatpicker.step2

import pl.training.workshop.exercises.e04_seatpicker.HallPlan
import pl.training.workshop.exercises.e04_seatpicker.Seat
import pl.training.workshop.shared.Money

/**
 * Krok 2: koszyk sam robi migawkę całego swojego stanu i sam ją przywraca. Migawka jest nieprzezroczysta
 * dla innych klas (prywatna klasa z danymi, publiczny jest tylko pusty typ), więc nikt poza koszykiem
 * nie zna ani nie kopiuje jego pól. Nowe pole koszyka wystarczy dopisać w jednym miejscu: tu.
 */
class Cart(val plan: HallPlan) {
    private val seats = mutableListOf<Seat>()
    var ownGlasses = false
    var voucher: String? = null

    /** Migawka koszyka: tylko koszyk ją tworzy i tylko koszyk ją czyta. */
    sealed interface Memento

    /**
     * Treść migawki. W Javie to prywatne pola i prywatny konstruktor klasy Memento, ale w Kotlinie
     * klasa zewnętrzna nie widzi prywatnych składowych klasy zagnieżdżonej - stąd prywatna klasa koszyka.
     */
    private class Snapshot(seats: List<Seat>, val ownGlasses: Boolean, val voucher: String?) : Memento {
        val seats: List<Seat> = seats.toList()
    }

    fun snapshot(): Memento = Snapshot(seats, ownGlasses, voucher)

    fun restore(memento: Memento) {
        val snapshot = memento as Snapshot
        seats.clear()
        seats.addAll(snapshot.seats)
        ownGlasses = snapshot.ownGlasses
        voucher = snapshot.voucher
    }

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
