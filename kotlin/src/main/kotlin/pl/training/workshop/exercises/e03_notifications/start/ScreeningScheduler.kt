package pl.training.workshop.exercises.e03_notifications.start

import pl.training.workshop.exercises.e03_notifications.Customer
import pl.training.workshop.exercises.e03_notifications.Screening

/**
 * Start: zmiana repertuaru sama wie, kogo i jak powiadomic. Po kazdej zmianie scheduler na sztywno
 * wola kolejno: powiadomienia klientow, liste oczekujacych i statystyki. Wybor kanalu (push, potem SMS,
 * potem e-mail) jest lancuchem if skopiowanym do obu metod, a kazda para kanal x rodzaj to osobna klasa.
 * Nowy odbiorca zmian (np. kasa biletowa) albo nowy kanal to edycja tej klasy w dwoch miejscach.
 */
class ScreeningScheduler(private val journal: Journal, private val waitlist: Waitlist, private val stats: ChangeStats) {
    private val current = HashMap<String, Screening>()
    private val cancelled = HashSet<String>()

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
        for (customer in ticketHolders) {
            if (!customer.pushToken.isNullOrBlank() && !customer.pushToken.startsWith("expired-")) {
                PushMovedNotice(journal, before, after).send(customer)
            } else if (!customer.phone.isNullOrBlank()) {
                SmsMovedNotice(journal, before, after).send(customer)
            } else if (!customer.email.isNullOrBlank()) {
                EmailMovedNotice(journal, before, after).send(customer)
            } else {
                journal.note("UNDELIVERED ${customer.name} (brak kanalu)")
            }
        }
        waitlist.screeningMoved(after)
        stats.moved()
    }

    fun cancel(screeningId: String, reason: String?, ticketHolders: List<Customer>) {
        if (reason.isNullOrBlank()) {
            throw IllegalArgumentException("reason must not be blank")
        }
        val screening = live(screeningId)
        cancelled += screeningId
        journal.note("SCHEDULE ${screening.title} ${screening.start} odwolany: $reason")
        for (customer in ticketHolders) {
            if (!customer.pushToken.isNullOrBlank() && !customer.pushToken.startsWith("expired-")) {
                PushCancelledNotice(journal, screening, reason).send(customer)
            } else if (!customer.phone.isNullOrBlank()) {
                SmsCancelledNotice(journal, screening, reason).send(customer)
            } else if (!customer.email.isNullOrBlank()) {
                EmailCancelledNotice(journal, screening, reason).send(customer)
            } else {
                journal.note("UNDELIVERED ${customer.name} (brak kanalu)")
            }
        }
        waitlist.screeningCancelled(screening)
        stats.cancelled()
    }

    private fun live(screeningId: String): Screening {
        val screening = current[screeningId] ?: throw IllegalArgumentException("unknown screening: $screeningId")
        if (screeningId in cancelled) {
            throw IllegalArgumentException("screening already cancelled: ${screening.title}")
        }
        return screening
    }
}
