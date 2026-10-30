package pl.training.workshop.exercises.e03_notifications.step2

import pl.training.workshop.exercises.e03_notifications.Customer

/**
 * Krok 2: jawny koniec lancucha. Zamiast "wypadniecia" z ostatniego else wpis o niedostarczeniu
 * jest ogniwem - mozna go podmienic (np. na zgloszenie do kasy) bez edycji pozostalych.
 */
class UndeliveredLink(private val journal: Journal) : Channel {
    override fun deliver(customer: Customer, subject: String, body: String) {
        journal.note("UNDELIVERED ${customer.name} (brak kanalu)")
    }
}
