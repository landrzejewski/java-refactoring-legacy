package pl.training.workshop.exercises.e03_notifications.start

import pl.training.workshop.exercises.e03_notifications.Customer
import pl.training.workshop.exercises.e03_notifications.Screening

/** Start: lista oczekujacych na bilety - po przeniesieniu zachowuje kolejnosc, po odwolaniu jest czyszczona. */
class Waitlist(private val journal: Journal, waiting: List<Customer>) {
    private val waiting = waiting.toMutableList()

    fun screeningMoved(after: Screening) {
        journal.note("WAITLIST ${after.title}: ${waiting.size} oczekujacych zachowuje kolejnosc")
    }

    fun screeningCancelled(screening: Screening) {
        journal.note("WAITLIST ${screening.title}: lista wyczyszczona (${waiting.size})")
        waiting.clear()
    }
}
