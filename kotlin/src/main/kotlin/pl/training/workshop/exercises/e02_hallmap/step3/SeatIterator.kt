package pl.training.workshop.exercises.e02_hallmap.step3

/** Krok 3: przejście po miejscach w głąb, od lewej - bez zmian. */
internal class SeatIterator(root: HallGroup) : Iterator<Seat> {
    private val path = ArrayDeque<Iterator<HallElement>>()
    private var next: Seat? = null

    init {
        path.addLast(root.children.iterator())
        advance()
    }

    override fun hasNext(): Boolean = next != null

    override fun next(): Seat {
        val current = next ?: throw NoSuchElementException()
        advance()
        return current
    }

    private fun advance() {
        next = null
        while (path.isNotEmpty()) {
            val level = path.last()
            if (!level.hasNext()) {
                path.removeLast()
                continue
            }
            val element = level.next()
            if (element is HallGroup) {
                path.addLast(element.children.iterator())
            } else {
                next = element as Seat
                return
            }
        }
    }
}
