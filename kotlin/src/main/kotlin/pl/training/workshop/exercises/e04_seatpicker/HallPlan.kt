package pl.training.workshop.exercises.e04_seatpicker

/**
 * Stabilny kontrakt sceny: układ sali na jeden seans (Kraina Lodu 3D): liczba rzędów i miejsc,
 * pierwszy rząd VIP oraz miejsca już sprzedane.
 */
data class HallPlan(val rows: Int, val seatsPerRow: Int, val vipFromRow: Int, val taken: Set<Seat>) {
    fun exists(seat: Seat): Boolean = seat.row in 1..rows && seat.number in 1..seatsPerRow

    fun isTaken(seat: Seat): Boolean = seat in taken

    fun isVip(seat: Seat): Boolean = seat.row >= vipFromRow

    /** Rzędy w kolejności preferencji: od środka sali na zewnątrz, np. 6, 7, 5, 8, 4, ... */
    fun rowsFromMiddle(): List<Int> {
        val order = mutableListOf<Int>()
        val middle = (rows + 1) / 2
        var offset = 0
        while (order.size < rows) {
            if (middle + offset <= rows && middle + offset !in order) {
                order += middle + offset
            }
            if (middle - offset >= 1 && middle - offset !in order) {
                order += middle - offset
            }
            offset++
        }
        return order
    }

    companion object {
        /** Sala 12 x 10, VIP od rzędu 10, kilka miejsc sprzedanych. */
        fun standard(): HallPlan = HallPlan(
            12, 10, 10,
            setOf(Seat(6, 5), Seat(6, 6), Seat(7, 1), Seat(7, 2), Seat(10, 3))
        )

        /** Mała, prawie pełna sala 3 x 4: wolne są tylko pojedyncze miejsca, nigdzie dwa obok siebie. */
        fun almostFull(): HallPlan = HallPlan(
            3, 4, 3,
            setOf(
                Seat(1, 2), Seat(1, 4),
                Seat(2, 1), Seat(2, 3),
                Seat(3, 2), Seat(3, 3)
            )
        )
    }
}
