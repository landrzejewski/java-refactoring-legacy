package pl.training.workshop.exercises.e04_seatpicker

/** Stabilny kontrakt sceny: miejsce w sali zapisywane jako "rzad-numer", np. 6-4. */
data class Seat(val row: Int, val number: Int) {
    override fun toString(): String = "$row-$number"

    companion object {
        fun parse(text: String): Seat {
            // jak String.split w Javie: puste części na końcu są pomijane
            val parts = text.split("-").dropLastWhile { it.isEmpty() }
            if (parts.size != 2) {
                throw IllegalArgumentException("zly format miejsca: $text")
            }
            try {
                return Seat(parts[0].toInt(), parts[1].toInt())
            } catch (exception: NumberFormatException) {
                throw IllegalArgumentException("zly format miejsca: $text")
            }
        }
    }
}
