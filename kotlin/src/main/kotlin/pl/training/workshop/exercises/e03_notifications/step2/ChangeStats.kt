package pl.training.workshop.exercises.e03_notifications.step2

/** Krok 2: statystyki jako odbiorca zdarzen. */
class ChangeStats(private val journal: Journal) : ScreeningListener {
    private var moved = 0
    private var cancelled = 0

    override fun screeningMoved(event: ScreeningMoved) {
        moved++
        journal.note("STATS przeniesione=$moved odwolane=$cancelled")
    }

    override fun screeningCancelled(event: ScreeningCancelled) {
        cancelled++
        journal.note("STATS przeniesione=$moved odwolane=$cancelled")
    }
}
