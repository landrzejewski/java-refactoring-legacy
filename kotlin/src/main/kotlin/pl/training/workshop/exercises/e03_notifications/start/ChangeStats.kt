package pl.training.workshop.exercises.e03_notifications.start

/** Start: statystyki zmian repertuaru - licznik przeniesien i odwolan wypisywany po kazdej zmianie. */
class ChangeStats(private val journal: Journal) {
    private var moved = 0
    private var cancelled = 0

    fun moved() {
        moved++
        journal.note("STATS przeniesione=$moved odwolane=$cancelled")
    }

    fun cancelled() {
        cancelled++
        journal.note("STATS przeniesione=$moved odwolane=$cancelled")
    }
}
