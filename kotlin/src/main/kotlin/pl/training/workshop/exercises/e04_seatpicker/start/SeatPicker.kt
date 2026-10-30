package pl.training.workshop.exercises.e04_seatpicker.start

import pl.training.workshop.exercises.e04_seatpicker.HallPlan
import pl.training.workshop.exercises.e04_seatpicker.Seat
import pl.training.workshop.shared.Money

/**
 * Start: koszyk miejsc kasjera w jednej klasie. Operacje wybiera `when` po nazwie,
 * a cofanie to drugi łańcuch `if` z odwrotnością każdej operacji. Historia trzyma jeden
 * rekord "na wszystko" (pola używane zależnie od operacji). Przed automatyczną propozycją miejsc
 * koszyk jest zabezpieczany ręcznym punktem kontrolnym: kopią pól do zmiennych lokalnych.
 * Punkt kontrolny powstał, gdy koszyk miał tylko miejsca i okulary; voucher doszedł później
 * i nikt nie dopisał go do kopii.
 */
class SeatPicker(private val plan: HallPlan) {
    private val seats = mutableListOf<Seat>()
    private var ownGlasses = false
    private var voucher: String? = null
    private val history = ArrayDeque<Change>()

    /** Jeden rekord na wszystkie rodzaje zmian: które pola coś znaczą, zależy od `op`. */
    private data class Change(
        val op: String,
        val seat: Seat?,
        val other: Seat?,
        val index: Int,
        val flag: Boolean,
        val text: String?,
        val seatsBefore: List<Seat>?,
    )

    fun apply(line: String) {
        val parts = line.trim().split(WHITESPACE)
        val op = parts[0].uppercase()
        when (op) {
            "SELECT" -> {
                val seat = Seat.parse(arg(parts, 1))
                select(seat)
                history.addLast(Change(op, seat, null, 0, false, null, null))
            }
            "DESELECT" -> {
                val seat = Seat.parse(arg(parts, 1))
                val index = seats.indexOf(seat)
                if (index < 0) {
                    throw IllegalArgumentException("miejsca $seat nie ma w koszyku")
                }
                seats.removeAt(index)
                history.addLast(Change(op, seat, null, index, false, null, null))
            }
            "SWAP" -> {
                val from = Seat.parse(arg(parts, 1))
                val to = Seat.parse(arg(parts, 2))
                val index = seats.indexOf(from)
                if (index < 0) {
                    throw IllegalArgumentException("miejsca $from nie ma w koszyku")
                }
                checkFree(to)
                seats[index] = to
                history.addLast(Change(op, from, to, index, false, null, null))
            }
            "GLASSES" -> {
                val own = parseGlasses(arg(parts, 1))
                history.addLast(Change(op, null, null, 0, ownGlasses, null, null))
                ownGlasses = own
            }
            "VOUCHER" -> {
                val code = parseVoucher(arg(parts, 1))
                history.addLast(Change(op, null, null, 0, false, voucher, null))
                voucher = code
            }
            "CLEAR" -> {
                history.addLast(Change(op, null, null, 0, false, null, seats.toList()))
                seats.clear()
            }
            "AUTO" -> autoArrange(parseCount(arg(parts, 1)))
            "UNDO" -> undo()
            else -> throw IllegalArgumentException("nieznana operacja: ${parts[0]}")
        }
    }

    private fun select(seat: Seat) {
        checkFree(seat)
        if (seats.size >= LIMIT) {
            throw IllegalStateException("limit $LIMIT miejsc")
        }
        seats += seat
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

    /**
     * Automatyczna propozycja: czyści koszyk i wybiera `count` wolnych miejsc obok siebie
     * w najlepszym rzędzie. Gdy się nie uda, przywraca koszyk z punktu kontrolnego.
     */
    private fun autoArrange(count: Int) {
        if (count < 1 || count > LIMIT) {
            throw IllegalArgumentException("limit $LIMIT miejsc")
        }
        // punkt kontrolny: kopia pól do zmiennych lokalnych
        val seatsBefore = seats.toList()
        val glassesBefore = ownGlasses
        seats.clear()
        ownGlasses = false
        voucher = null
        try {
            for (row in plan.rowsFromMiddle()) {
                val first = freeRun(row, count)
                if (first > 0) {
                    for (i in 0 until count) {
                        seats += Seat(row, first + i)
                    }
                    history.addLast(Change("AUTO", null, null, 0, glassesBefore, null, seatsBefore))
                    return
                }
            }
            throw IllegalStateException("brak $count miejsc obok siebie")
        } catch (exception: IllegalStateException) {
            seats.clear()
            seats.addAll(seatsBefore)
            ownGlasses = glassesBefore
            throw exception
        }
    }

    /** Numer pierwszego miejsca ciągu `count` wolnych miejsc w rzędzie albo 0. */
    private fun freeRun(row: Int, count: Int): Int {
        var run = 0
        for (number in 1..plan.seatsPerRow) {
            run = if (plan.isTaken(Seat(row, number))) 0 else run + 1
            if (run == count) {
                return number - count + 1
            }
        }
        return 0
    }

    private fun undo() {
        if (history.isEmpty()) {
            throw IllegalStateException("nie ma czego cofnac")
        }
        val change = history.removeLast()
        if (change.op == "SELECT") {
            seats.remove(change.seat!!)
        } else if (change.op == "DESELECT") {
            seats.add(change.index, change.seat!!)
        } else if (change.op == "SWAP") {
            seats[change.index] = change.seat!!
        } else if (change.op == "GLASSES") {
            ownGlasses = change.flag
        } else if (change.op == "VOUCHER") {
            voucher = change.text
        } else if (change.op == "CLEAR") {
            seats.addAll(change.seatsBefore!!)
        } else if (change.op == "AUTO") {
            seats.clear()
            seats.addAll(change.seatsBefore!!)
            ownGlasses = change.flag
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
        val WHITESPACE = Regex("\\s+")

        fun arg(parts: List<String>, index: Int): String {
            if (parts.size <= index) {
                throw IllegalArgumentException("brak argumentu operacji ${parts[0].uppercase()}")
            }
            return parts[index]
        }

        fun parseGlasses(text: String): Boolean = when (text.lowercase()) {
            "own" -> true
            "rent" -> false
            else -> throw IllegalArgumentException("okulary: own albo rent")
        }

        fun parseVoucher(text: String): String? {
            if (text.equals("none", ignoreCase = true)) {
                return null
            }
            if (text == "KINO10") {
                return text
            }
            throw IllegalArgumentException("nieznany voucher: $text")
        }

        fun parseCount(text: String): Int = try {
            text.toInt()
        } catch (exception: NumberFormatException) {
            throw IllegalArgumentException("zla liczba miejsc: $text")
        }
    }
}
