package pl.training.workshop.exercises.e10_showtime.step1

/**
 * Krok 1: drzwi znają tylko własny stan (zamknięte, przeszkoda) i sterownik sali. Nie pamiętają już
 * tytułu filmu ani tego, co ma się stać po zamknięciu - tylko zgłaszają zdarzenie.
 */
class Doors(private val log: DeviceLog, private val hall: HallMediator) {
    private var closed = false
    private var blocked = false

    val isClosed: Boolean
        get() = closed

    fun close() {
        if (closed) {
            throw IllegalStateException("doors already closed")
        }
        if (blocked) {
            log.add("doors: blocked")
            hall.doorsBlocked()
            return
        }
        closed = true
        log.add("doors: closed")
        hall.doorsClosed()
    }

    fun open() {
        closed = false
        log.add("doors: open")
    }

    /** Czujnik: coś stoi w świetle drzwi. */
    fun obstacle() {
        blocked = true
        log.add("doors: obstacle")
    }

    /** Czujnik: przejście wolne. */
    fun cleared() {
        if (!blocked) {
            throw IllegalStateException("doors are not blocked")
        }
        blocked = false
        log.add("doors: cleared")
        hall.doorsCleared()
    }
}
