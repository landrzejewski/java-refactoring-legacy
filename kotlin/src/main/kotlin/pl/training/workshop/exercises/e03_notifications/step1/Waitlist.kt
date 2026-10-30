package pl.training.workshop.exercises.e03_notifications.step1

import pl.training.workshop.exercises.e03_notifications.Customer

/** Krok 1: lista oczekujacych jako odbiorca zdarzen - ta sama logika, inna sygnatura. */
class Waitlist(private val journal: Journal, waiting: List<Customer>) : ScreeningListener {
    private val waiting = waiting.toMutableList()

    override fun screeningMoved(event: ScreeningMoved) {
        journal.note("WAITLIST ${event.after.title}: ${waiting.size} oczekujacych zachowuje kolejnosc")
    }

    override fun screeningCancelled(event: ScreeningCancelled) {
        journal.note("WAITLIST ${event.screening.title}: lista wyczyszczona (${waiting.size})")
        waiting.clear()
    }
}
