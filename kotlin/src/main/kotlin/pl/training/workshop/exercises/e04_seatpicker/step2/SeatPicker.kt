package pl.training.workshop.exercises.e04_seatpicker.step2

import pl.training.workshop.exercises.e04_seatpicker.HallPlan
import pl.training.workshop.exercises.e04_seatpicker.Seat

/**
 * Krok 2: wywołujący - tłumaczy linię skryptu na obiekt polecenia, wykonuje je i odkłada na stos.
 * UNDO zdejmuje ostatnie polecenie i prosi je o cofnięcie; drugi łańcuch if zniknął, a każda
 * linia skryptu dostaje własny obiekt z własnymi danymi do cofnięcia.
 */
class SeatPicker(plan: HallPlan) {
    private val cart = Cart(plan)
    private val history = ArrayDeque<Command>()

    fun apply(line: String) {
        val parts = line.trim().split(WHITESPACE)
        val op = parts[0].uppercase()
        if (op == "UNDO") {
            undo()
            return
        }
        val command = parse(op, parts)
        command.execute()
        history.addLast(command)
    }

    private fun parse(op: String, parts: List<String>): Command = when (op) {
        "SELECT" -> SelectSeat(cart, Seat.parse(arg(parts, 1)))
        "DESELECT" -> DeselectSeat(cart, Seat.parse(arg(parts, 1)))
        "SWAP" -> SwapSeats(cart, Seat.parse(arg(parts, 1)), Seat.parse(arg(parts, 2)))
        "GLASSES" -> SetGlasses(cart, parseGlasses(arg(parts, 1)))
        "VOUCHER" -> SetVoucher(cart, parseVoucher(arg(parts, 1)))
        "CLEAR" -> ClearSeats(cart)
        "AUTO" -> AutoArrange(cart, parseCount(arg(parts, 1)))
        else -> throw IllegalArgumentException("nieznana operacja: ${parts[0]}")
    }

    private fun undo() {
        if (history.isEmpty()) {
            throw IllegalStateException("nie ma czego cofnac")
        }
        history.removeLast().undo()
    }

    fun state(): String = cart.state()

    private companion object {
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
