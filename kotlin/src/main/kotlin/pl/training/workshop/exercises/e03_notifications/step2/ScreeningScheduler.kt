package pl.training.workshop.exercises.e03_notifications.step2

import pl.training.workshop.exercises.e03_notifications.Customer
import pl.training.workshop.exercises.e03_notifications.Screening

/**
 * Krok 2: scheduler zapisuje zmiane i publikuje zdarzenie do zarejestrowanych odbiorcow.
 * Kolejnosc powiadamiania to kolejnosc rejestracji - to czesc kontraktu (powiadomienia, lista, statystyki).
 * Wyjatek odbiorcy przerywa powiadamianie kolejnych - tak bylo w start i tak zostaje.
 */
class ScreeningScheduler(private val journal: Journal) {
    private val listeners = mutableListOf<ScreeningListener>()
    private val current = HashMap<String, Screening>()
    private val cancelled = HashSet<String>()

    fun subscribe(listener: ScreeningListener) {
        listeners += listener
    }

    fun unsubscribe(listener: ScreeningListener) {
        listeners -= listener
    }

    fun register(screening: Screening) {
        current[screening.id] = screening
    }

    fun move(screeningId: String, newStart: String, newHall: String, ticketHolders: List<Customer>) {
        if (newStart.isBlank()) {
            throw IllegalArgumentException("new start must not be blank")
        }
        if (newHall.isBlank()) {
            throw IllegalArgumentException("hall must not be blank")
        }
        val before = live(screeningId)
        val after = before.copy(start = newStart, hall = newHall)
        current[screeningId] = after
        journal.note("SCHEDULE ${before.title} ${before.start} -> $newStart sala $newHall")
        val event = ScreeningMoved(before, after, ticketHolders.toList())
        for (listener in listeners.toList()) {
            listener.screeningMoved(event)
        }
    }

    fun cancel(screeningId: String, reason: String?, ticketHolders: List<Customer>) {
        if (reason.isNullOrBlank()) {
            throw IllegalArgumentException("reason must not be blank")
        }
        val screening = live(screeningId)
        cancelled += screeningId
        journal.note("SCHEDULE ${screening.title} ${screening.start} odwolany: $reason")
        val event = ScreeningCancelled(screening, reason, ticketHolders.toList())
        for (listener in listeners.toList()) {
            listener.screeningCancelled(event)
        }
    }

    private fun live(screeningId: String): Screening {
        val screening = current[screeningId] ?: throw IllegalArgumentException("unknown screening: $screeningId")
        if (screeningId in cancelled) {
            throw IllegalArgumentException("screening already cancelled: ${screening.title}")
        }
        return screening
    }
}
